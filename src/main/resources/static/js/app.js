/* app.js - 게시판 기능 개선 (View/Edit 모드 분리 및 토글) */
const TOKEN_KEY = "stockInsightToken";

// --- [1] 토큰 및 인증 관리 ---
function getToken() { return localStorage.getItem(TOKEN_KEY); }
function setToken(token) { localStorage.setItem(TOKEN_KEY, token); }
function clearToken() { localStorage.removeItem(TOKEN_KEY); }

function logout() {
    clearToken();
    window.location.href = "/index.html";
}

function requireAuth() {
    if (!getToken()) {
        alert("로그인이 필요한 서비스입니다.");
        window.location.href = "/login.html";
        throw new Error("Unauthorized");
    }
}

// --- [2] API 요청 유틸리티 ---
async function apiRequest(method, path, body, auth = false) {
    const headers = { "Content-Type": "application/json" };
    if (auth) {
        const accessToken = getToken();
        if (accessToken) headers.Authorization = `Bearer ${accessToken}`;
    }

    const res = await fetch(path, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    });

    let payload = null;
    const text = await res.text();
    if (text) {
        try { payload = JSON.parse(text); } catch { payload = text; }
    }

    if (!res.ok) {
        throw new Error(payload?.message || res.statusText);
    }
    return payload;
}

// --- [3] UI 상태 관리 ---
function initGlobalUI() {
    updateAuthUI();

    // 모바일 메뉴 토글
    const toggleBtn = document.getElementById("menuToggle");
    const mobileMenu = document.getElementById("mobileMenu");
    if (toggleBtn && mobileMenu) {
        toggleBtn.addEventListener("click", () => {
            mobileMenu.classList.toggle("open");
        });
    }

    if (getToken()) updateUserInfo();
}

function updateAuthUI() {
    const isLoggedIn = !!getToken();
    const guestGroups = document.querySelectorAll(".auth-guest");
    const userGroups = document.querySelectorAll(".auth-user");

    if (isLoggedIn) {
        guestGroups.forEach(el => el.classList.add("hidden"));
        userGroups.forEach(el => el.classList.remove("hidden"));
    } else {
        guestGroups.forEach(el => el.classList.remove("hidden"));
        userGroups.forEach(el => el.classList.add("hidden"));
    }
}

async function updateUserInfo() {
    const displayEl = document.getElementById("userNameDisplay");
    if (!displayEl) return;
    try {
        const me = await apiRequest("GET", "/me", null, true);
        displayEl.textContent = `${me}님`;
    } catch { }
}

// --- [4] 페이지별 기능 ---

// 1. 로그인
function bindLoginForm() {
    const form = document.getElementById("loginForm");
    const message = document.getElementById("loginMessage");
    if (!form) return;

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        message.textContent = "로그인 중...";
        const formData = new FormData(form);
        try {
            const data = await apiRequest("POST", "/auth/login", {
                email: formData.get("email"),
                password: formData.get("password"),
            });
            setToken(data.accessToken);
            window.location.href = "/index.html";
        } catch (error) {
            message.textContent = `로그인 실패: ${error.message}`;
        }
    });
}

// 2. 회원가입
function bindSignupForm() {
    const form = document.getElementById("signupForm");
    const message = document.getElementById("signupMessage");
    if (!form) return;

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        message.textContent = "가입 처리 중...";
        const formData = new FormData(form);
        try {
            await apiRequest("POST", "/auth/signup", {
                email: formData.get("email"),
                password: formData.get("password"),
                nickname: formData.get("nickname"),
            });
            alert("가입 성공! 로그인 해주세요.");
            window.location.href = "/login.html";
        } catch (error) {
            message.textContent = `가입 실패: ${error.message}`;
        }
    });
}

// 3. 리포트 페이지
function initReportsPage() {
    requireAuth();
    bindReportGenerateForm();
    loadReports();
}

function toggleReportList() {
    const container = document.getElementById("reportListContainer");
    const btn = document.getElementById("listToggleBtn");
    if (!container || !btn) return;
    if (container.classList.contains("hidden")) {
        container.classList.remove("hidden");
        btn.textContent = "▲ 접기";
    } else {
        container.classList.add("hidden");
        btn.textContent = "▼ 펼치기";
    }
}

function bindReportGenerateForm() {
    const form = document.getElementById("reportGenerateForm");
    const message = document.getElementById("reportGenerateMessage");
    if (!form) return;

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        message.textContent = "분석 중...";
        const formData = new FormData(form);
        try {
            const report = await apiRequest("POST", "/reports/generate", {
                ticker: formData.get("ticker"),
                market: formData.get("market"),
            }, true);
            message.textContent = "완료!";
            form.reset();
            renderReportDetail(report);
            loadReports();
            document.getElementById("reportDetail").scrollIntoView({ behavior: "smooth" });
        } catch (error) {
            message.textContent = `오류: ${error.message}`;
        }
    });
}

async function loadReports() {
    const list = document.getElementById("reportList");
    if (!list) return;
    try {
        const reports = await apiRequest("GET", "/reports", null, true);
        if (!reports.length) {
            list.innerHTML = "<p class='muted'>리포트가 없습니다.</p>";
            return;
        }
        list.innerHTML = reports.map(r => `
            <div class="list-item">
                <div style="display:flex; align-items:center; gap:1rem;">
                    <h4 style="margin:0;">${r.ticker}</h4>
                    <span class="muted" style="font-size:0.85rem;">${r.market} · ${new Date(r.createdAt).toLocaleDateString()}</span>
                </div>
                <div class="list-actions">
                    <button class="button ghost" onclick="viewReport(${r.id})">보기</button>
                    <button class="button ghost" style="color:var(--danger);" onclick="deleteReport(${r.id})">삭제</button>
                </div>
            </div>
        `).join("");
    } catch (e) { list.innerHTML = "로드 실패"; }
}

async function viewReport(id) {
    try {
        const report = await apiRequest("GET", `/reports/${id}`, null, true);
        renderReportDetail(report);
        document.getElementById("reportDetail").scrollIntoView({ behavior: "smooth", block: "start" });
    } catch (e) { alert(e.message); }
}

function renderReportDetail(report) {
    const detail = document.getElementById("reportDetail");
    const result = report.resultJson ? JSON.parse(report.resultJson) : {};
    detail.innerHTML = `
        <div style="margin-bottom:2rem;">
            <span class="muted">${report.market}</span>
            <h1 style="font-size: 2.5rem; margin:0.5rem 0;">${report.ticker}</h1>
            <p style="font-size: 1.1rem; color:var(--text-main);">${report.summary || "요약 정보 없음"}</p>
        </div>
        <div style="background:#f8fafc; padding:2rem; border-radius:12px; border:1px solid var(--border);">
            <h3 style="margin-top:0;">📋 상세 분석</h3>
            <div style="white-space:pre-wrap; line-height:1.8; color:var(--text-main);">${result.reportText || "내용 없음"}</div>
        </div>
    `;
}

async function deleteReport(id) {
    if (!confirm("삭제하시겠습니까?")) return;
    try {
        await apiRequest("DELETE", `/reports/${id}`, null, true);
        loadReports();
        const detail = document.getElementById("reportDetail");
        if(detail) detail.innerHTML = "<div style='padding:3rem; text-align:center; color:#94a3b8;'>삭제되었습니다.</div>";
    } catch (e) { alert(e.message); }
}

// 4. 게시판 페이지 (개선됨)
function initPostsPage() {
    requireAuth();
    bindPostCreateForm();
    loadAllPosts();
    loadPosts();
}

// [NEW] 전체 게시글 목록 토글
function toggleAllPostList() {
    const container = document.getElementById("allPostListContainer");
    const btn = document.getElementById("allListToggleBtn");
    if (!container || !btn) return;

    if (container.classList.contains("hidden")) {
        container.classList.remove("hidden");
        btn.textContent = "▲ 접기";
    } else {
        container.classList.add("hidden");
        btn.textContent = "▼ 펼치기";
    }
}

// [NEW] 내 게시글 목록 토글
function toggleMyPostList() {
    const container = document.getElementById("postListContainer");
    const btn = document.getElementById("myListToggleBtn");
    if (!container || !btn) return;

    if (container.classList.contains("hidden")) {
        container.classList.remove("hidden");
        btn.textContent = "▲ 접기";
    } else {
        container.classList.add("hidden");
        btn.textContent = "▼ 펼치기";
    }
}

function bindPostCreateForm() {
    const form = document.getElementById("postCreateForm");
    const message = document.getElementById("postMessage");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        message.textContent = "업로드 중...";
        try {
            await apiRequest("POST", "/posts", {
                title: form.title.value, content: form.content.value
            }, true);
            message.textContent = "등록 완료!";
            form.reset();
            loadAllPosts();
            loadPosts();
        } catch (err) { message.textContent = err.message; }
    });
}

async function loadAllPosts() {
    const list = document.getElementById("allPostList");
    if (!list) return;
    try {
        const posts = await apiRequest("GET", "/posts/public", null, true);
        if (!posts.length) {
            list.innerHTML = "<p class='muted'>게시글이 없습니다.</p>";
            return;
        }
        list.innerHTML = posts.map(p => `
            <div class="list-item">
                <div style="flex:1; cursor:pointer;" onclick="viewPost(${p.id})">
                    <h4 style="margin:0 0 0.25rem 0;">${p.title} ${p.isOwner ? "<span class='muted' style='font-size:0.8rem;'>(내 글)</span>" : ""}</h4>
                    <span class="muted" style="font-size:0.85rem;">${new Date(p.createdAt).toLocaleDateString()}</span>
                </div>
                <div class="list-actions">
                    <button class="button ghost" onclick="viewPost(${p.id})">보기</button>
                </div>
            </div>
        `).join("");
    } catch (e) { list.innerHTML = "로드 실패"; }
}


async function loadPosts() {
    const list = document.getElementById("postList");
    if(!list) return;
    try {
        const posts = await apiRequest("GET", "/posts", null, true);
        if (!posts.length) {
            list.innerHTML = "<p class='muted'>게시글이 없습니다.</p>";
            return;
        }
        // 제목에 onclick 이벤트 추가 (조회용)
        list.innerHTML = posts.map(p => `
            <div class="list-item">
                <div style="flex:1; cursor:pointer;" onclick="viewPost(${p.id})">
                    <h4 style="margin:0 0 0.25rem 0;">${p.title}</h4>
                    <span class="muted" style="font-size:0.85rem;">${new Date(p.createdAt).toLocaleDateString()}</span>
                </div>
                <div class="list-actions">
                    <button class="button ghost" onclick="viewPost(${p.id})">보기</button>
                    <button class="button ghost" onclick="editPostMode(${p.id})">수정</button>
                    <button class="button ghost" style="color:var(--danger);" onclick="deletePost(${p.id})">삭제</button>
                </div>
            </div>
        `).join("");
    } catch (e) { list.innerHTML = "로드 실패"; }
}

// [NEW] 게시글 상세 조회 (읽기 모드)
async function viewPost(id) {
    try {
        const post = await apiRequest("GET", `/posts/public/${id}`, null, true);
        const panel = document.getElementById("postDetailPanel");

        // 읽기 전용 HTML 주입
        panel.innerHTML = `
            <div style="padding:1rem 0;">
                <div style="border-bottom:1px solid var(--border); padding-bottom:1rem; margin-bottom:1.5rem;">
                    <h2 style="margin:0 0 0.5rem 0;">${post.title}</h2>
                    <span class="muted">${new Date(post.createdAt).toLocaleString()}</span>
                </div>
                <div style="white-space:pre-wrap; line-height:1.6; font-size:1.05rem; min-height:150px;">${post.content}</div>
                ${post.isOwner ? `
                    <div style="margin-top:2rem; padding-top:1rem; border-top:1px solid var(--border);">
                        <button class="button ghost" onclick="editPostMode(${post.id})">수정하기</button>
                    </div>
                ` : `
                    <div class="muted" style="margin-top:2rem; padding-top:1rem; border-top:1px solid var(--border); font-size:0.9rem;">
                        작성자만 수정할 수 있습니다.
                    </div>
                `}
            </div>
        `;
        panel.scrollIntoView({ behavior: "smooth", block: "start" });
    } catch (error) { alert(error.message); }
}

// [NEW] 게시글 수정 모드 (폼 렌더링)
async function editPostMode(id) {
    try {
        const post = await apiRequest("GET", `/posts/${id}`, null, true);
        const panel = document.getElementById("postDetailPanel");

        // 수정 폼 HTML 주입
        panel.innerHTML = `
            <h3 style="margin-bottom:1.5rem;">게시글 수정</h3>
            <form id="dynamicEditForm" class="form">
                <label>제목 <input type="text" name="title" value="${post.title}" required /></label>
                <label>내용 <textarea name="content" rows="8" required>${post.content}</textarea></label>
                <div class="button-row" style="margin-top:1rem; display:flex; gap:1rem;">
                    <button class="button primary" type="submit">수정 완료</button>
                    <button class="button ghost" type="button" onclick="viewPost(${post.id})">취소</button>
                </div>
            </form>
        `;

        // 동적으로 생성된 폼에 이벤트 리스너 연결
        const form = document.getElementById("dynamicEditForm");
        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData(form);
            try {
                await apiRequest("PUT", `/posts/${id}`, {
                    title: formData.get("title"),
                    content: formData.get("content"),
                }, true);
                alert("수정되었습니다.");
                loadAllPosts();
                loadPosts(); // 목록 갱신 (제목 변경 반영)
                viewPost(id); // 다시 읽기 모드로 전환
            } catch (err) { alert(`수정 실패: ${err.message}`); }
        });

        panel.scrollIntoView({ behavior: "smooth", block: "start" });

    } catch (error) { alert(error.message); }
}

async function deletePost(id) {
    if (!confirm("정말 삭제하시겠습니까?")) return;
    try {
        await apiRequest("DELETE", `/posts/${id}`, null, true);
        loadAllPosts();
        loadPosts();
        // 상세 내용 초기화
        document.getElementById("postDetailPanel").innerHTML =
            "<div style='padding:3rem; text-align:center; color:#94a3b8;'>삭제되었습니다.</div>";
    } catch (error) { alert(error.message); }
}

// --- [5] 공통 실행 ---
document.addEventListener("DOMContentLoaded", () => {
    initGlobalUI();
});
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('submitButton').addEventListener('click', async function (event) {
        // 폼 제출 이벤트 막기 !
        event.preventDefault();

        try {
            // 첫 번째 유효성 검사: 제목 입력 확인
            const name = document.getElementById('title').value;
            if (!name) {
                await window.showModal("제목을 입력해 주세요.", false);
                return false;
            }

            // 두 번째 유효성 검사: 내용 입력 확인
            const email = document.getElementById('content').value;
            if (!email) {
                await window.showModal("내용을 입력해 주세요.", false);
                return false;
            }

            // 모든 유효성 검사가 통과한 경우 폼 제출 (form 태그의 id값 넣어야 됨)
            document.getElementById('wordBookForm').submit();

        } catch (error) {
            console.error('유효성 검사 도중 오류가 발생했습니다:', error);
        }
    });
});
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('submitButton').addEventListener('click', async function (event) {
        event.preventDefault();

        try {
            const title = document.getElementById('title').value.trim();
            if (!title || title.length < 2 || title.length > 50) {
                await window.showModal("게시글 제목은 2자 이상 50자 이하로 <br>입력하세요.", false);
                return false;
            }

            const content = document.getElementById('content').value.trim();
            if (!content || content.length < 2 || content.length > 300) {
                await window.showModal("게시글 내용은 2자 이상 300자 이하로 <br>입력하세요.", false);
                return false;
            }

            document.getElementById('boardForm').submit();

        } catch (error) {
            console.error('유효성 검사 도중 오류가 발생했습니다:', error);
        }
    });
});

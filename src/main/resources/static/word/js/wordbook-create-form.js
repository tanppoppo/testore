document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('submitButton').addEventListener('click', async function (event) {
        event.preventDefault();

        try {
            const name = document.getElementById('title').value;
            if (!name || name.length < 2 || name.length > 30) {
                await window.showModal("단어장 이름은 2자 이상 30자 이하로 <br>입력하세요.", false);
                return false;
            }

            const content = document.getElementById('content').value;
            if (!content || content.length < 2 || content.length > 100) {
                await window.showModal("단어장 설명은 2자 이상 100자 이하로 <br>입력하세요", false);
                return false;
            }

            document.getElementById('wordBookForm').submit();

        } catch (error) {
            console.error('유효성 검사 도중 오류가 발생했습니다:', error);
        }
    });
});
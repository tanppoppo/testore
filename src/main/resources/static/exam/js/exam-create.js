document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('submitButton').addEventListener('click', async function (event) {
        event.preventDefault();

        try {
            const title = document.getElementById('title').value.trim();
            const content = document.getElementById('content').value.trim();
            const passScore = document.getElementById('passScore').value;

            if (!title || title.length < 2 || title.length > 30) {
                await window.showModal("시험지 이름은 2자 이상 30자 이하로 <br>입력하세요.", false);
                return false;
            }

            if (!content || content.length < 2 || content.length > 100) {
                await window.showModal("시험지 설명은 2자 이상 100자 이하로 <br>입력하세요.", false);
                return false;
            }

            const passScoreInt = parseInt(passScore);
            if (isNaN(passScoreInt) || passScoreInt < 10 || passScoreInt > 100) {
                await window.showModal("합격 점수는 10에서 100사이의 숫자로 <br>입력하세요.", false);
                return false;
            }

            document.getElementById('examForm').submit();

        } catch (error) {
            console.error("유효성 검사 도중 오류가 발생했습니다. : ", error);
        }
    });
});
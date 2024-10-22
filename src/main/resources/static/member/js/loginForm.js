document.addEventListener('DOMContentLoaded', function () {

    document.getElementById('submitButton').addEventListener('click', async function (event) {
        event.preventDefault();

        try {
            const email = document.getElementById('email').value;
            if (!email || email.length < 2 || email.length > 100) {
                await window.showModal("이메일은 2자 이상 100자 이하로 입력하세요.", false);
                return false;
            }

            const password = document.getElementById('memberPassword').value;
            if (!password || password.length < 8 || password.length > 20) {
                await window.showModal("비밀번호는 8자 이상 20자 이하로 입력하세요.", false);
                return false;
            }

            document.getElementById('loginForm').submit();

        } catch (error) {
            console.error('유효성 검사 도중 오류가 발생했습니다:', error);
        }
    });
});

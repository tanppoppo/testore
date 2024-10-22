document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('submitButton').addEventListener('click', async function (event) {
        event.preventDefault();

        try {
            const email = document.getElementById('email').value;
            if (!email || email.length < 2 || email.length > 100) {
                await window.showModal("이메일은 2자 이상 100자 이하로 입력하세요.", false);
                return false;
            }

            const nickname = document.getElementById('nickname').value;
            if (!nickname || nickname.length < 2 || nickname.length > 50) {
                await window.showModal("닉네임은 2자 이상 50자 이하로 입력하세요.", false);
                return false;
            }

            const password = document.getElementById('memberPassword').value;
            if (!password || password.length < 8 || password.length > 20) {
                await window.showModal("비밀번호는 8자 이상 20자 이하로 입력하세요.", false);
                return false;
            }

            const confirmPassword = document.getElementById('confirmPassword').value;
            if (password !== confirmPassword) {
                await window.showModal("비밀번호와 비밀번호 확인이 일치하지 <br>않습니다.", false);
                return false;
            }

            document.querySelector('form').submit();

        } catch (error) {
            console.error('유효성 검사 도중 오류가 발생했습니다:', error);
        }
    });
});

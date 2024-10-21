document.addEventListener('DOMContentLoaded', function () {

    const toastElement = document.querySelector('#toast');
    const modalElement = document.querySelector('#modal');

    const hideToastButton = document.querySelector('#hideToast');
    const confirmModalButton = document.querySelector('#confirmModal');
    const cancelModalButton = document.querySelector('#cancelModal');

    checkNotifications();
    setInterval(checkNotifications, 10000);

    if (hideToastButton) {
        hideToastButton.addEventListener('click', hideToast);
    }

    if (toastElement && window.getComputedStyle(toastElement).display === 'flex') {
        setTimeout(function () {
            toastElement.style.display = 'none';
        }, 3000)
    }

    function hideToast() {
        toastElement.style.display = 'none';
    }

    window.showToast = function showToast(content) {
        let toastContent = document.querySelector('#toastContent');
        toastElement.style.display = 'flex';
        toastContent.textContent = content;

        setTimeout(function () {
            toastElement.style.display = 'none';
        }, 3000)
    }

    window.showModal = function showModal(content, canCancel = true, link = null) {
        return new Promise((resolve, reject) => {

            const modal = document.getElementById('customModal');
            const modalContent = document.getElementById('modalContent');
            const confirmButton = document.getElementById('confirmModal');
            const cancelButton = document.getElementById('cancelModal');
            const linkButton = document.getElementById('linkButton');

            if (!modal || !modalContent) {
                console.error('모달 요소를 찾을 수 없습니다.');
                reject('모달 요소가 없음');
                return;
            }

            modalContent.innerHTML = content != null ? content : '내용 없음';

            if (link) {
                if (linkButton) {
                    linkButton.style.display = 'inline-flex';
                    linkButton.setAttribute('onclick', `location.href='${link}'`);
                }
                if (confirmButton) {
                    confirmButton.style.display = 'none';
                }
            } else {
                if (linkButton) {
                    linkButton.style.display = 'none';
                }
                if (confirmButton) {
                    confirmButton.style.display = 'inline-flex';
                }
            }

            if (canCancel) {
                if (cancelButton) {
                    cancelButton.style.display = 'inline-flex';
                    cancelButton.onclick = function () {
                        closeModal();
                        resolve(false);
                    };
                }
            } else {
                if (cancelButton) {
                    cancelButton.style.display = 'none';
                }
            }

            modal.classList.remove('hidden');
            modal.classList.add('flex');

            if (confirmButton) {

                confirmButton.replaceWith(confirmButton.cloneNode(true));
                const newConfirmButton = document.getElementById('confirmModal');

                newConfirmButton.onclick = function () {
                    closeModal();
                    resolve(true);
                };
            }
        });
    }

    window.closeModal = function closeModal() {
        const modal = document.getElementById('customModal');
        modal.classList.add('hidden');
        modal.classList.remove('flex');
    }

    function checkNotifications() {
        fetch('/member/notification/check')
            .then(response => response.json())
            .then(data => {
                if (data === true) {
                    document.querySelector('#noti-dot').classList.remove('hidden');
                } else {
                    document.querySelector('#noti-dot').classList.add('hidden');
                }
            })
            .catch(error => console.error(error));
    }

})
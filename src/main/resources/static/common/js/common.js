document.addEventListener('DOMContentLoaded', function () {

    const toastElement = document.querySelector('#toast');
    const modalElement = document.querySelector('#modal');

    const hideToastButton = document.querySelector('#hideToast');
    const confirmModalButton = document.querySelector('#confirmModal');
    const cancelModalButton = document.querySelector('#cancelModal');

    if (hideToastButton) {
        hideToastButton.addEventListener('click', hideToast);
    }
    if (confirmModalButton) {
        confirmModalButton.addEventListener('click', hideModal);
    }
    if (cancelModalButton) {
        cancelModalButton.addEventListener('click', hideModal);
    }

    if (toastElement && window.getComputedStyle(toastElement).display === 'flex') {
        setTimeout(function () {
            toastElement.style.display = 'none';
        }, 3000)
    }

    function hideToast() {
        toastElement.style.display = 'none';
    }

    function hideModal() {
        modalElement.style.display = 'none';
    }

    function showToast(content) {
        let toastContent = document.querySelector('#toastContent');
        toastElement.style.display = 'flex';
        toastContent.textContent = content;

        setTimeout(function () {
            toastElement.style.display = 'none';
        }, 3000)
    }

    function showModal(content) {
        let modalContent = document.querySelector('#modalContent');
        modalElement.style.display = 'flex';
        modalContent.textContent = content;

        setTimeout(function () {
            modalElement.style.display = 'none';
        }, 3000)
    }
})
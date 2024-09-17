document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('hideToast').addEventListener('click', hideToast);
    const toastElement = document.querySelector('#toast');

    if (window.getComputedStyle(toastElement).display === 'flex') {
        setTimeout(function () {
            toastElement.style.display = 'none';
        }, 3000)
    }

    function hideToast() {
        const toastElement = document.querySelector('#toast');
        toastElement.style.display = 'none';
    }

    function showToast() {
        const toastElement = document.querySelector('#toast');
        toastElement.style.display = 'flex';

        setTimeout(function () {
            toastElement.style.display = 'none';
        }, 3000)
    }
})
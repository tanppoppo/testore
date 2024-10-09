document.addEventListener('DOMContentLoaded', () => {
    const menuButtons = document.querySelectorAll('.menu-button');

    menuButtons.forEach(button => {
        button.addEventListener('click', () => {
            const menu = button.nextElementSibling;

            document.querySelectorAll('.menu').forEach(m => {
                if (m !== menu) {
                    m.classList.add('hidden');
                }
            });

            menu.classList.toggle('hidden');
        });
    });
});
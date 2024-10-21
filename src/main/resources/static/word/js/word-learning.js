document.addEventListener("DOMContentLoaded", function () {

    const wordDiv = document.getElementById('word');
    const curWordCountSpan = document.querySelector('#curWordCount');
    const allWordCountSpan = document.querySelector('#allWordCount');
    const prevButton = document.querySelector('#prevButton');
    const flipButton = document.querySelector('#flipButton');
    const nextButton = document.querySelector('#nextButton');

    let words = items;
    let currentIndex = 0;
    let showingWord = true;

    if (words.length > 0) {
        allWordCountSpan.textContent = words.length;
        showWord(currentIndex);
    }


    prevButton.addEventListener('click', () => {
        showPrev();
    });

    nextButton.addEventListener('click', () => {
        showNext();
    });

    flipButton.addEventListener('click', () => {
        showFlip();
    });

    function showWord(currentIndex) {
        const wordData = words[currentIndex];

        showingWord
            ? wordDiv.innerHTML = `${wordData.text1}`
            : wordDiv.innerHTML = `${wordData.text2}<div class="mt-6" style='font-size: clamp(0.7rem, 6vw, 2rem);'>${wordData.text3}</div>`;
        curWordCountSpan.textContent = currentIndex + 1;
    }

    function showPrev() {
        if (currentIndex > 0) {
            currentIndex--;
            showingWord = true;
            showWord(currentIndex);
        } else if (currentIndex == 0) {
            currentIndex = words.length - 1;
            showingWord = true;
            showWord(currentIndex);
        }
    }

    function showNext() {
        if (currentIndex < words.length - 1) {
            currentIndex++;
            showingWord = true;
            showWord(currentIndex);
        } else if (currentIndex == words.length - 1) {
            currentIndex = 0;
            showingWord = true;
            showWord(currentIndex);
        }
    }

    function showFlip() {
        showingWord = !showingWord;
        showWord(currentIndex);
    }
})
document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('#questionForm');
    const submitButton = form.querySelector('#submitButton');
    let problemCount = 1;
    let addedTypesPerProblem = {};

    addedTypesPerProblem[problemCount] = {};
    addNewQuestionSet(problemCount);

    document.querySelector('#addButton').addEventListener('click', function () {
        if (validateCurrentInputs(problemCount)) {
            problemCount++;

            addedTypesPerProblem[problemCount] = {};
            addNewQuestionSet(problemCount);
        } else {
            window.showModal("문제 질문 1개, 선택지는 최소 2개 이상<br/>추가하고 답을 선택해주세요.", false);
            return false;
        }
    });

    submitButton.addEventListener('click', function (event) {
        if (!validateAllQuestions()) {
            event.preventDefault();
            window.showModal("문제 질문 1개, 선택지는 최소 2개 이상<br/>추가 후 답을 선택해주시고<br/>비어있지 않아야 합니다.", false);
        }
    });

    function addNewQuestionSet(number) {
        const questionLabel = document.createElement('label');
        questionLabel.textContent = `문제 ${number}`;
        questionLabel.classList.add('block', 'mt-14', 'mb-2', 'text-sm', 'font-medium', 'text-gray-900');

        const newSelect = document.createElement('select');
        newSelect.innerHTML = `
                  <option value="" selected>선택하기</option>
                  <option value="desc">단락 설명</option>
                  <option value="content">단락 내용</option>
                  <option value="question">문제 질문</option>
                  <option value="choice">문제 선택지</option>
                  `
        ;
        newSelect.classList.add('bg-gray-50', 'border', 'border-gray-300', 'text-gray-900', 'text-sm', 'rounded-lg', 'block', 'w-full', 'p-2.5', 'mb-4');

        const inputsContainer = document.createElement('div');
        inputsContainer.className = `inputs-container-${number}`;

        form.insertBefore(questionLabel, submitButton);
        form.insertBefore(newSelect, submitButton);
        form.insertBefore(inputsContainer, submitButton);

        newSelect.addEventListener('change', function (event) {
            addInputField(event.target, number);
        });
    }

    function addInputField(select, number) {
        const type = select.value;
        const container = document.querySelector(`.inputs-container-${number}`);
        const wrapper = document.createElement('div');
        wrapper.className = 'flex items-center space-x-4 mb-2';

        let element;
        let elementClass = 'w-full ';

        if (type == 'content') {
            element = document.createElement('textarea');
        } else {
            element = document.createElement('input');
            element.type = 'text';
        }

        switch (type) {
            case 'desc':
                element.placeholder = '단락 설명 입력';
                element.name = `${type}_${number}`;
                elementClass += 'border-b border-gray-400 p-3 focus:border-gray-600 focus:outline-none';
                break;
            case 'content':
                element.placeholder = '단락 내용 입력';
                element.name = `${type}_${number}`;
                elementClass += 'h-36 border-2 border-gray-300 mt-3 p-3 mx-0 rounded-lg focus:border-gray-600 focus:outline-none';
                break;
            case 'question':
                element.placeholder = '문제 질문 입력';
                element.name = `${type}_${number}`;
                elementClass += 'border-b border-gray-400 p-3 focus:border-gray-600 focus:outline-none';
                break;
            case 'choice':
                addedTypesPerProblem[number]['choice'] = addedTypesPerProblem[number]['choice'] || 0;
                addedTypesPerProblem[number]['choice']++;
                element.placeholder = `선택지${addedTypesPerProblem[number]['choice']} 입력`;
                element.name = `${type}_${number}_${addedTypesPerProblem[number]['choice']}`;
                elementClass += 'border-b border-gray-400 p-3 focus:border-gray-600 focus:outline-none';
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.name = `answer_${number}`;
                checkbox.value = addedTypesPerProblem[number]['choice'];
                checkbox.className = 'w-5 h-5 ml-2 accent-green-600 rounded';
                wrapper.appendChild(checkbox);
                break;
        }

        element.className = elementClass;
        wrapper.appendChild(element);

        const deleteBtn = document.createElement('button');
        deleteBtn.className = 'text-red-500 hover:text-red-700';
        deleteBtn.innerHTML = `
            <svg class="w-[16px] h-[16px] text-gray-500" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
              <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18 17.94 6M18 18 6.06 6"/>
            </svg>
            `;

        deleteBtn.onclick = function () {
            wrapper.remove();

            if (type === 'choice' && addedTypesPerProblem[number]['choice'] > 0) {
                addedTypesPerProblem[number]['choice']--;
            } else {
                addedTypesPerProblem[number][type] = false;
            }
            updateChoiceOrder(container, number);
            updateSelectOptions(number);
        };

        wrapper.appendChild(deleteBtn);
        container.appendChild(wrapper);

        select.value = '';

        if (type !== 'choice') {
            addedTypesPerProblem[number][type] = true;
        }

        updateSelectOptions(number);
    }

    function updateSelectOptions(number) {
        const select = document.querySelector(`.inputs-container-${number}`).previousElementSibling;
        select.querySelectorAll('option').forEach(option => {
            const value = option.value;
            if (value === 'choice') {
                option.disabled = addedTypesPerProblem[number]['choice'] >= 5;
            } else {
                option.disabled = addedTypesPerProblem[number][value];
            }
        });
    }

    function validateCurrentInputs(number) {
        const inputsContainer = document.querySelector(`.inputs-container-${number}`);
        const inputs = inputsContainer.querySelectorAll('input, textarea');
        let questionCount = 0, choiceCount = 0, checkboxChecked = false;
        let allFieldsFilled = true;

        inputs.forEach(input => {
            if (input.name.startsWith('question')) questionCount++;
            if (input.name.startsWith('choice')) choiceCount++;
            if (input.type === 'checkbox' && input.checked) checkboxChecked = true;
            if (input.value.trim() === '') allFieldsFilled = false;
        });

        return questionCount >= 1 && choiceCount >= 2 && checkboxChecked && allFieldsFilled;
    }

    function validateAllQuestions() {
        for (let i = 1; i <= problemCount; i++) {
            if (!validateCurrentInputs(i)) {
                return false;
            }
        }
        return true;
    }

    function updateChoiceOrder(container, number) {
        const allChoices = container.querySelectorAll('input[name*="choice"]');
        const allCheckboxes = container.querySelectorAll('input[type="checkbox"]');
        allChoices.forEach((choiceInput, index) => {
            choiceInput.placeholder = `선택지${index + 1} 입력`;
            choiceInput.name = `choice_${number}_${index + 1}`;
            allCheckboxes[index].value = index + 1;
        });
    }
});

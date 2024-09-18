document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('#questionForm');
    const submitButton = form.querySelector('#submitButton');
    let problemCount = 1;
    let addedTypes = {};

    addNewQuestionSet(problemCount);

    document.querySelector('#addButton').addEventListener('click', function () {
        if (validateCurrentInputs(problemCount)) {
            problemCount++;
            addedTypes = {};
            addNewQuestionSet(problemCount);
        } else {
            alert('문제 질문 1개, 선택지는 최소 2개 이상 추가해야 합니다.');
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
                  <option value="choices">문제 선택지</option>
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
        const wrapper = document.createElement('div')
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
            case 'choices':
                addedTypes['choices'] = addedTypes['choices'] || 0;
                addedTypes['choices']++;
                element.placeholder = `선택지${addedTypes['choices']} 입력`;
                element.name = `${type}_${number}_${addedTypes['choices']}`;
                elementClass += 'border-b border-gray-400 p-3 focus:border-gray-600 focus:outline-none';
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.name = `answer_${number}`;
                checkbox.value = addedTypes['choices'];
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
            `

        deleteBtn.onclick = function () {
            wrapper.remove();

            if (type === 'choices' && addedTypes['choices'] > 0) {
                addedTypes['choices']--;
            } else {
                addedTypes[type] = false;
            }
            updateChoicesOrder(container, number);
            updateSelectOptions();
        };

        wrapper.appendChild(deleteBtn);
        container.appendChild(wrapper);

        select.value = '';

        if (type !== 'choices') {
            addedTypes[type] = true;
        }

        updateSelectOptions();
    }

    function updateSelectOptions() {
        document.querySelectorAll('select').forEach(select => {
            select.querySelectorAll('option').forEach(option => {
                const value = option.value;
                if (value === 'choices') {
                    option.disabled = addedTypes['choices'] >= 5;
                } else {
                    option.disabled = addedTypes[value];
                }
            });
        });
    }

    function validateCurrentInputs(number) {
        const inputsContainer = document.querySelector(`.inputs-container-${number}`);
        const inputs = inputsContainer.querySelectorAll('input');
        let questionCount = 0, choiceCount = 0;

        inputs.forEach(input => {
            if (input.name.startsWith('question')) questionCount++;
            if (input.name.startsWith('choices')) choiceCount++;
        });

        return questionCount >= 1 && choiceCount >= 2;
    }

    function updateChoicesOrder(container, number) {
        const allChoices = container.querySelectorAll('input[name*="choices"]');
        const allCheckboxes = container.querySelectorAll('input[type="checkbox"]');
        allChoices.forEach((choiceInput, index) => {
            choiceInput.placeholder = `선택지${index + 1} 입력`;
            choiceInput.name = `choices_${number}_${index + 1}`;
            allCheckboxes[index].value = index + 1;
        });
    }
});
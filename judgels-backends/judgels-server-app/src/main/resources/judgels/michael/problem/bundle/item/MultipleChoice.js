function refreshChoiceFormValues(choices) {
  choices.querySelectorAll('.choice:not(.choice-template)').forEach((choice, i) => {
    choice.querySelector('input[type=checkbox]').value = i;
  });
}

function addEventListenersToChoice(choices, choice) {
  choice.querySelector('.choice-remove-button').addEventListener('click', event => {
    event.preventDefault();
    choice.remove();
    refreshChoiceFormValues(choices);
  });
}

function addEventListenersToAddChoiceForm(choices) {
  var addButton = choices.querySelector('.choice-add-button');
  addButton.addEventListener('click', event => {
    var newChoice = choices.querySelector('.choice-template').cloneNode(true);
    var addForm = choices.querySelector('.choice-add-form');
    newChoice.classList.remove('choice-template');
    newChoice.classList.remove('hidden');
    newChoice.querySelectorAll('input').forEach(input => input.disabled = false);
    choices.insertBefore(newChoice, addForm);
    addEventListenersToChoice(choices, newChoice);
    refreshChoiceFormValues(choices);
  });
}

function addEventListenersToChoices() {
  var choices = document.getElementById('choices');
  choices.querySelectorAll('.choice:not(.choice-template)').forEach(choice => {
    addEventListenersToChoice(choices, choice);
  });
  addEventListenersToAddChoiceForm(choices);
}

document.addEventListener('DOMContentLoaded', () => {
  addEventListenersToChoices();
}, false);

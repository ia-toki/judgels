function addEventListeners() {
  var refreshTestGroupFormValues = tg => {
    var inputValues = [];
    var outputValues = [];

    tg.querySelectorAll('.tc').forEach(tc => {
      inputValues.push(tc.querySelector('.tc-input').value);
      outputValues.push(tc.querySelector('.tc-output').value);
    });

    tg.querySelector('input[name=testCaseInputs]').setAttribute('value', inputValues.join(','));
    tg.querySelector('input[name=testCaseOutputs]').setAttribute('value', outputValues.join(','));
  };

  var addEventListenersToTestCase = (tg, tc) => {
    tc.querySelector('.tc-remove-button').addEventListener('click', event => {
      event.preventDefault();
      tc.remove();
      refreshTestGroupFormValues(tg);
    });
  };

  var addEventListenersToAddTestCaseForm = tg => {
    tg.querySelector('.tc-add-button').addEventListener('click', event => {
      event.preventDefault();

      var newTestCase = tg.querySelector('tbody tr').cloneNode(true);
      var addForm = tg.querySelector('.tc-add-form');
      var inputValue = addForm.querySelector('.tc-input').value;
      var outputValue = addForm.querySelector('.tc-output').value;

      if (!inputValue) {
        alert('No files selected! Please upload test data files first.');
        return;
      }

      newTestCase.classList.add('tc');
      newTestCase.removeAttribute('style');
      newTestCase.querySelector('.tc-input').setAttribute('value', inputValue);
      newTestCase.querySelector('.tc-output').setAttribute('value', outputValue);
      addEventListenersToTestCase(tg, newTestCase);
      tg.querySelector('tbody').insertBefore(newTestCase, addForm);

      refreshTestGroupFormValues(tg);
    });
  };

  var addEventListenersToTestGroup = id => {
    var tg = document.getElementById(id);
    tg.querySelectorAll('.tc').forEach(tc => {
      addEventListenersToTestCase(tg, tc);
    });
    addEventListenersToAddTestCaseForm(tg);
  };

  document.addEventListener('DOMContentLoaded', () => {
    addEventListenersToTestGroup('sample-test-data');
    addEventListenersToTestGroup('test-data');
  }, false);
}

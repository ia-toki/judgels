function refreshTestGroupFormValues(testGroup) {
  var inputFiles = [];
  var outputFiles = [];

  testGroup.querySelectorAll('.test-case:not(.test-case-template)').forEach(testCase => {
    inputFiles.push(testCase.querySelector('.test-case-input').value);
    outputFiles.push(testCase.querySelector('.test-case-output').value);
  });

  testGroup.querySelector('input[name=testCaseInputs]').value = inputFiles.join(',');
  testGroup.querySelector('input[name=testCaseOutputs]').value = outputFiles.join(',');
}

function refreshSubtaskIdsFormValues(subtaskAssignment) {
  var subtaskIds = [];

  subtaskAssignment.querySelectorAll('input[type=checkbox]').forEach(checkbox => {
    if (checkbox.checked) {
      subtaskIds.push(checkbox.value);
    }
  });

  subtaskAssignment.querySelector('input[type=hidden]').value = subtaskIds.join(',');
}

function refreshTestGroupNumbering() {
  document.querySelectorAll('.test-group:not(.test-group-template)').forEach((testGroup, i) => {
    testGroup.querySelector('div span').innerHTML = 'Test group ' + (i+1);
  });
}

function addEventListenersToTestCase(testGroup, testCase, withSubtaskAssignment) {
  testCase.querySelector('.test-case-remove-button').addEventListener('click', event => {
    event.preventDefault();

    if (withSubtaskAssignment) {
      var subtaskAssignment = testCase.nextElementSibling;
      testCase.remove();
      subtaskAssignment.remove();
    } else {
      testCase.remove();
    }

    refreshTestGroupFormValues(testGroup);
  });
}

function addEventListenersToAddTestCaseForm(testGroup, withSubtaskAssignment) {
  testGroup.querySelector('.test-case-add-button').addEventListener('click', event => {
    event.preventDefault();

    var newTestCase = testGroup.querySelector('.test-case-template').cloneNode(true);
    var addForm = testGroup.querySelector('.test-case-add-form');
    var inputFile = addForm.querySelector('.test-case-input').value;
    var outputFile = addForm.querySelector('.test-case-output').value;

    if (!inputFile) {
      alert('No files selected! Please upload test data files first.');
      return;
    }

    newTestCase.classList.remove('test-case-template');
    newTestCase.querySelector('.test-case-input').value = inputFile;
    newTestCase.querySelector('.test-case-output').value = outputFile;
    addEventListenersToTestCase(testGroup, newTestCase, withSubtaskAssignment);
    testGroup.querySelector('tbody').insertBefore(newTestCase, addForm);

    if (withSubtaskAssignment) {
      var newSubtaskAssignment = testGroup.querySelector('.subtask-assignment-template').cloneNode(true);
      newSubtaskAssignment.classList.remove('subtask-assignment-template');
      newSubtaskAssignment.querySelectorAll('input[type=hidden]').forEach(input => input.disabled = false);
      testGroup.querySelector('tbody').insertBefore(newSubtaskAssignment, addForm);
    }

    refreshTestGroupFormValues(testGroup);
  });
}

function addEventListenersToSubtaskAssignment(subtaskAssignment) {
  subtaskAssignment.querySelectorAll('input[type=checkbox]').forEach(checkbox => {
    checkbox.addEventListener('click', event => {
      refreshSubtaskIdsFormValues(subtaskAssignment);
    })
  });
}

function addEventListenersToRemoveTestGroupButton(testGroup) {
  testGroup.querySelector('.test-group-remove-button').addEventListener('click', event => {
    event.preventDefault();
    testGroup.remove();
    refreshTestGroupNumbering();
  });
}

function addEventListenersToTestGroup(testGroup, withSubtaskAssignment) {
  testGroup.querySelectorAll('.test-case:not(.test-case-template)').forEach(testCase => {
    addEventListenersToTestCase(testGroup, testCase, false);
  });
  addEventListenersToAddTestCaseForm(testGroup, false);

  if (withSubtaskAssignment) {
    addEventListenersToSubtaskAssignment(testGroup.querySelector('.subtask-assignment'));
    addEventListenersToRemoveTestGroupButton(testGroup);
  }
}

function addEventListenersToAddTestGroupForm(testGroups) {
  var addButton = testGroups.querySelector('.test-group-add-button');
  addButton.addEventListener('click', event => {
    var newTestGroup = testGroups.querySelector('.test-group-template').cloneNode(true);
    newTestGroup.classList.remove('test-group-template');
    newTestGroup.querySelectorAll('input[type=hidden]').forEach(input => input.disabled = false);
    testGroups.insertBefore(newTestGroup, addButton);
    addEventListenersToTestGroup(newTestGroup, true);
    refreshTestGroupNumbering();
  });
}

function addEventListenersToSampleTestData() {
  var testGroup = document.getElementById('sample-test-data');
  addEventListenersToTestGroup(testGroup, false);
}

function addEventListenersToTestData() {
  var testGroup = document.getElementById('test-data');
  addEventListenersToTestGroup(testGroup, false);
}

function addEventListenersToSampleTestDataWithSubtasks() {
  var testGroup = document.getElementById('sample-test-data');
  testGroup.querySelectorAll('.test-case:not(.test-case-template)').forEach(testCase => {
    addEventListenersToTestCase(testGroup, testCase, true);
    addEventListenersToSubtaskAssignment(testCase.nextElementSibling);
  });
  addEventListenersToAddTestCaseForm(testGroup, true);
}

function addEventListenersToTestDataWithSubtasks() {
  var testGroups = document.getElementById('test-groups');
  testGroups.querySelectorAll('.test-group:not(.test-group-template)').forEach(testGroup => {
    addEventListenersToTestGroup(testGroup, true);
  });
  addEventListenersToAddTestGroupForm(testGroups);
}

function configure() {
  document.addEventListener('DOMContentLoaded', () => {
    addEventListenersToSampleTestData();
    addEventListenersToTestData();
  }, false);
}

function configureWithSubtasks() {
  document.addEventListener('DOMContentLoaded', () => {
    addEventListenersToSampleTestDataWithSubtasks();
    addEventListenersToTestDataWithSubtasks();
  }, false);
}

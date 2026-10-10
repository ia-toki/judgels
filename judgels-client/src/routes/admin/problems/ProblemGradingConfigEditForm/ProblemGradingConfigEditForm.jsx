import {
  Button,
  Callout,
  Card,
  Checkbox,
  FormGroup,
  HTMLSelect,
  HTMLTable,
  InputGroup,
  Intent,
} from '@blueprintjs/core';
import { Plus, Trash } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { getGradingConfigFields, toConfig, toFormValues, validate } from './gradingConfigForm';

import './ProblemGradingConfigEditForm.scss';

function SubtaskAssignment({ label, subtaskCount, subtaskIds, disabled, onChange }) {
  const toggle = id => onChange(subtaskIds.includes(id) ? subtaskIds.filter(i => i !== id) : [...subtaskIds, id]);

  return (
    <Flex role="group" aria-label={label} gap={2} alignItems="center" flexWrap="wrap">
      <small>Assign to subtasks:</small>
      {[...Array(subtaskCount).keys()].map(idx => (
        <Checkbox
          key={idx + 1}
          inline
          className="problem-grading-config-edit-form__subtask"
          label={idx + 1}
          checked={subtaskIds.includes(idx + 1)}
          disabled={disabled}
          onChange={() => toggle(idx + 1)}
        />
      ))}
    </Flex>
  );
}

function FileSelect({ label, filenames, value, onChange }) {
  return (
    <HTMLSelect aria-label={label} value={value} onChange={e => onChange(e.target.value)}>
      <option value="">{label}...</option>
      {filenames.map(filename => (
        <option key={filename} value={filename}>
          {filename}
        </option>
      ))}
    </HTMLSelect>
  );
}

function TestCases({ label, testCases, hasOutput, canEdit, filenames, onChange, renderSubtaskAssignment }) {
  const [input, setInput] = useState('');
  const [output, setOutput] = useState('');

  const selectInput = newInput => {
    setInput(newInput);

    // An input file is usually paired with the output file of the same name.
    const pairedOutput = newInput.replace(/\.in$/, '.out');
    if (pairedOutput !== newInput && filenames.includes(pairedOutput)) {
      setOutput(pairedOutput);
    }
  };

  const addTestCase = () => {
    onChange([...testCases, { input, output: hasOutput ? output : '', subtaskIds: [] }]);
    setInput('');
    setOutput('');
  };

  const columnCount = 1 + (hasOutput ? 1 : 0) + (canEdit ? 1 : 0);

  return (
    <HTMLTable compact aria-label={label} className="problem-grading-config-edit-form__test-cases">
      <thead>
        <tr>
          <th>Input</th>
          {hasOutput && <th>Output</th>}
          {canEdit && <th />}
        </tr>
      </thead>
      <tbody>
        {testCases.length === 0 && !canEdit && (
          <tr>
            <td colSpan={columnCount}>
              <small>No test cases.</small>
            </td>
          </tr>
        )}
        {testCases.flatMap((testCase, idx) => [
          <tr key={idx}>
            <td>{testCase.input}</td>
            {hasOutput && <td>{testCase.output}</td>}
            {canEdit && (
              <td className="problem-grading-config-edit-form__action">
                <Button
                  small
                  minimal
                  intent={Intent.DANGER}
                  icon={<Trash />}
                  aria-label={`Remove ${testCase.input}`}
                  onClick={() => onChange(testCases.filter((_, i) => i !== idx))}
                />
              </td>
            )}
          </tr>,
          renderSubtaskAssignment && (
            <tr key={`${idx}-subtasks`}>
              <td colSpan={columnCount}>
                {renderSubtaskAssignment(testCase, subtaskIds =>
                  onChange(testCases.map((tc, i) => (i === idx ? { ...tc, subtaskIds } : tc)))
                )}
              </td>
            </tr>
          ),
        ])}
        {canEdit && (
          <tr>
            <td>
              <FileSelect label="Input" filenames={filenames} value={input} onChange={selectInput} />
            </td>
            {hasOutput && (
              <td>
                <FileSelect label="Output" filenames={filenames} value={output} onChange={setOutput} />
              </td>
            )}
            <td className="problem-grading-config-edit-form__action">
              <Button
                small
                icon={<Plus />}
                aria-label="Add test case"
                disabled={!input || (hasOutput && !output)}
                onClick={addTestCase}
              />
            </td>
          </tr>
        )}
      </tbody>
    </HTMLTable>
  );
}

export default function ProblemGradingConfigEditForm({
  engine,
  config,
  testDataFiles,
  helperFiles,
  canEdit,
  onSubmit,
  onAutoPopulate,
}) {
  const fields = getGradingConfigFields(engine);
  const testDataFilenames = testDataFiles.map(file => file.name);

  const [values, setValues] = useState(() => toFormValues(engine, config));
  const [error, setError] = useState(undefined);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isAutoPopulating, setIsAutoPopulating] = useState(false);

  const setValue = (name, value) => setValues(v => ({ ...v, [name]: value }));

  const setTestGroup = (idx, testGroup) => {
    setValue(
      'testGroups',
      values.testGroups.map((tg, i) => (i === idx ? { ...tg, ...testGroup } : tg))
    );
  };

  const subtaskCount = fields.subtasks
    ? Math.max(
        values.subtaskPoints.length,
        ...values.sampleTestCases.flatMap(testCase => testCase.subtaskIds),
        ...values.testGroups.flatMap(testGroup => testGroup.subtaskIds)
      )
    : 0;

  const submit = async e => {
    e.preventDefault();

    const validationError = validate(engine, values);
    setError(validationError);
    if (validationError) {
      return;
    }

    setIsSubmitting(true);
    try {
      await onSubmit(toConfig(engine, config, values));
    } finally {
      setIsSubmitting(false);
    }
  };

  // Only the test data is taken from the proposal, so that the other unsaved edits are kept.
  const autoPopulate = async () => {
    setIsAutoPopulating(true);
    try {
      const { sampleTestCases, testGroups, subtaskPoints } = toFormValues(engine, await onAutoPopulate());
      setValues(v => ({ ...v, sampleTestCases, testGroups, ...(fields.subtasks ? { subtaskPoints } : {}) }));
    } finally {
      setIsAutoPopulating(false);
    }
  };

  const renderTextInput = (name, label, labelInfo) => (
    <FormGroup label={label} labelInfo={labelInfo && `(${labelInfo})`}>
      <InputGroup
        aria-label={label}
        value={values[name]}
        disabled={!canEdit}
        onChange={e => setValue(name, e.target.value)}
      />
    </FormGroup>
  );

  const renderHelperSelect = (name, label) => (
    <FormGroup label={label}>
      <HTMLSelect
        aria-label={label}
        value={values[name]}
        disabled={!canEdit}
        onChange={e => setValue(name, e.target.value)}
      >
        <option value="">(none)</option>
        {/* A helper the config names stays selectable even after its file is deleted. */}
        {[...new Set([...helperFiles.map(file => file.name), values[name]])]
          .filter(filename => filename)
          .map(filename => (
            <option key={filename} value={filename}>
              {filename}
            </option>
          ))}
      </HTMLSelect>
    </FormGroup>
  );

  const renderSampleTestCases = () => (
    <FormGroup label="Sample test data">
      <Card>
        <TestCases
          label="Sample test cases"
          testCases={values.sampleTestCases}
          hasOutput={fields.output}
          canEdit={canEdit}
          filenames={testDataFilenames}
          onChange={testCases => setValue('sampleTestCases', testCases)}
          renderSubtaskAssignment={
            fields.subtasks &&
            ((testCase, onChange) => (
              <SubtaskAssignment
                label={`Subtasks of ${testCase.input}`}
                subtaskCount={subtaskCount}
                subtaskIds={testCase.subtaskIds}
                disabled={!canEdit}
                onChange={onChange}
              />
            ))
          }
        />
      </Card>
    </FormGroup>
  );

  const renderTestGroups = () => (
    <FormGroup label="Test data">
      <Flex flexDirection="column" gap={2}>
        {values.testGroups.map((testGroup, idx) => {
          const label = fields.subtasks ? `Test group ${idx + 1}` : 'Test cases';
          return (
            <Card key={idx}>
              {fields.subtasks && (
                <Flex justifyContent="space-between" alignItems="center">
                  <strong>{label}</strong>
                  {canEdit && (
                    <Button
                      small
                      minimal
                      intent={Intent.DANGER}
                      icon={<Trash />}
                      aria-label={`Remove test group ${idx + 1}`}
                      onClick={() =>
                        setValue(
                          'testGroups',
                          values.testGroups.filter((_, i) => i !== idx)
                        )
                      }
                    />
                  )}
                </Flex>
              )}
              <TestCases
                label={label}
                testCases={testGroup.testCases}
                hasOutput={fields.output}
                canEdit={canEdit}
                filenames={testDataFilenames}
                onChange={testCases => setTestGroup(idx, { testCases })}
              />
              {fields.subtasks && (
                <SubtaskAssignment
                  label={`Subtasks of test group ${idx + 1}`}
                  subtaskCount={subtaskCount}
                  subtaskIds={testGroup.subtaskIds}
                  disabled={!canEdit}
                  onChange={subtaskIds => setTestGroup(idx, { subtaskIds })}
                />
              )}
            </Card>
          );
        })}
        {fields.subtasks && canEdit && (
          <ActionButtons>
            <Button
              small
              icon={<Plus />}
              text="New test group"
              onClick={() => setValue('testGroups', [...values.testGroups, { testCases: [], subtaskIds: [] }])}
            />
          </ActionButtons>
        )}
      </Flex>
    </FormGroup>
  );

  const renderSubtaskPoints = () => (
    <FormGroup label="Subtasks">
      <Card>
        <HTMLTable compact aria-label="Subtasks">
          <thead>
            <tr>
              <th>No</th>
              <th>Points</th>
            </tr>
          </thead>
          <tbody>
            {values.subtaskPoints.map((points, idx) => (
              <tr key={idx}>
                <td>{idx + 1}</td>
                <td>
                  <InputGroup
                    small
                    aria-label={`Points of subtask ${idx + 1}`}
                    value={points}
                    disabled={!canEdit}
                    onChange={e =>
                      setValue(
                        'subtaskPoints',
                        values.subtaskPoints.map((p, i) => (i === idx ? e.target.value : p))
                      )
                    }
                  />
                </td>
              </tr>
            ))}
          </tbody>
        </HTMLTable>
      </Card>
    </FormGroup>
  );

  return (
    <form className="problem-grading-config-edit-form" onSubmit={submit}>
      {canEdit && (
        <ActionButtons>
          <Button
            small
            text={
              fields.subtasks ? 'Auto-populate test data from TCFrame format' : 'Auto-populate test data from filenames'
            }
            loading={isAutoPopulating}
            onClick={autoPopulate}
          />
        </ActionButtons>
      )}
      {fields.limits && renderTextInput('timeLimit', 'Time limit', 'milliseconds')}
      {fields.limits && renderTextInput('memoryLimit', 'Memory limit', 'kilobytes')}
      {fields.sourceFileFieldKeys && renderTextInput('sourceFileFieldKeys', 'Source file keys', 'comma-separated')}
      {renderSampleTestCases()}
      {renderTestGroups()}
      {fields.subtasks && renderSubtaskPoints()}
      {fields.customScorer && renderHelperSelect('customScorer', 'Custom scorer')}
      {fields.communicator && renderHelperSelect('communicator', 'Communicator')}
      {error && <Callout intent={Intent.DANGER}>{error}</Callout>}
      {canEdit && (
        <ActionButtons justifyContent="end">
          <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={isSubmitting} />
        </ActionButtons>
      )}
    </form>
  );
}

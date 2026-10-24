import { Button, Callout, Checkbox, FormGroup, HTMLTable, InputGroup, Intent } from '@blueprintjs/core';
import { Plus, Trash } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { OptionSelect } from '../../../../components/OptionSelect/OptionSelect';
import { getGradingConfigFields, toConfig, toFormValues, validate } from './gradingConfigForm';

import './ProblemGradingConfigEditForm.scss';

function SubtaskAssignment({ label, subtaskCount, subtaskIds, onChange }) {
  const toggle = id => onChange(subtaskIds.includes(id) ? subtaskIds.filter(i => i !== id) : [...subtaskIds, id]);

  return (
    <Flex role="group" aria-label={label} flexDirection="column" gap={1}>
      <small>Assign to subtasks:</small>
      <Flex gap={2} alignItems="center" flexWrap="wrap">
        {[...Array(subtaskCount).keys()].map(idx => (
          <Checkbox
            key={idx + 1}
            className="problem-grading-config-edit-form__subtask"
            label={idx + 1}
            checked={subtaskIds.includes(idx + 1)}
            onChange={() => toggle(idx + 1)}
          />
        ))}
      </Flex>
    </Flex>
  );
}

function FileSelect({ label, filenames, value, onChange }) {
  return (
    <OptionSelect
      options={filenames}
      value={value}
      getOptionName={filename => filename}
      label={label}
      placeholder={`${label}...`}
      noResultsText="No files found."
      onChange={onChange}
    />
  );
}

function TestCases({ label, testCases, hasOutput, filenames, onChange, renderSubtaskAssignment }) {
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

  const columnCount = 2 + (hasOutput ? 1 : 0);

  return (
    <HTMLTable compact aria-label={label} className="problem-grading-config-edit-form__test-cases">
      <thead>
        <tr>
          <th>Input</th>
          {hasOutput && <th>Output</th>}
          <th />
        </tr>
      </thead>
      <tbody>
        {testCases.flatMap((testCase, idx) => [
          <tr key={idx}>
            <td>{testCase.input}</td>
            {hasOutput && <td>{testCase.output}</td>}
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
          </tr>,
          renderSubtaskAssignment && (
            <tr key={`${idx}-subtasks`} className="problem-grading-config-edit-form__subtask-row">
              <td colSpan={columnCount}>
                {renderSubtaskAssignment(testCase, subtaskIds =>
                  onChange(testCases.map((tc, i) => (i === idx ? { ...tc, subtaskIds } : tc)))
                )}
              </td>
            </tr>
          ),
        ])}
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
      </tbody>
    </HTMLTable>
  );
}

export default function ProblemGradingConfigEditForm({
  engine,
  config,
  testDataFiles,
  helperFiles,
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
      <InputGroup aria-label={label} value={values[name]} onChange={e => setValue(name, e.target.value)} />
    </FormGroup>
  );

  const renderHelperSelect = (name, label) => {
    // A helper the config names stays selectable even after its file is deleted.
    const filenames = [...new Set([...helperFiles.map(file => file.name), values[name]])].filter(filename => filename);

    return (
      <FormGroup label={label}>
        <OptionSelect
          options={['', ...filenames]}
          value={values[name]}
          getOptionName={filename => filename || '(none)'}
          label={label}
          placeholder="(none)"
          onChange={filename => setValue(name, filename)}
        />
      </FormGroup>
    );
  };

  const renderSampleTestCases = () => (
    <FormGroup label="Sample test data">
      <ContentCard compact>
        <TestCases
          label="Sample test cases"
          testCases={values.sampleTestCases}
          hasOutput={fields.output}
          filenames={testDataFilenames}
          onChange={testCases => setValue('sampleTestCases', testCases)}
          renderSubtaskAssignment={
            fields.subtasks &&
            ((testCase, onChange) => (
              <SubtaskAssignment
                label={`Subtasks of ${testCase.input}`}
                subtaskCount={subtaskCount}
                subtaskIds={testCase.subtaskIds}
                onChange={onChange}
              />
            ))
          }
        />
      </ContentCard>
    </FormGroup>
  );

  const renderTestGroups = () => (
    <FormGroup label="Test data">
      <Flex flexDirection="column" gap={2}>
        {values.testGroups.map((testGroup, idx) => {
          const label = fields.subtasks ? `Test group ${idx + 1}` : 'Test cases';
          return (
            <ContentCard key={idx} compact>
              {fields.subtasks && (
                <Flex className="problem-grading-config-edit-form__test-group-heading" alignItems="center" gap={2}>
                  <h5>{label}</h5>
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
                </Flex>
              )}
              <Flex flexDirection="column" gap={2}>
                <TestCases
                  label={label}
                  testCases={testGroup.testCases}
                  hasOutput={fields.output}
                  filenames={testDataFilenames}
                  onChange={testCases => setTestGroup(idx, { testCases })}
                />
                {fields.subtasks && (
                  <SubtaskAssignment
                    label={`Subtasks of test group ${idx + 1}`}
                    subtaskCount={subtaskCount}
                    subtaskIds={testGroup.subtaskIds}
                    onChange={subtaskIds => setTestGroup(idx, { subtaskIds })}
                  />
                )}
              </Flex>
            </ContentCard>
          );
        })}
        {fields.subtasks && (
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
      <ContentCard compact>
        <HTMLTable compact aria-label="Subtasks" className="problem-grading-config-edit-form__subtask-points">
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
                    aria-label={`Points of subtask ${idx + 1}`}
                    value={points}
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
      </ContentCard>
    </FormGroup>
  );

  return (
    <Flex asChild flexDirection="column" gap={6}>
      <form onSubmit={submit}>
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
        <Flex flexDirection="column" gap={2}>
          {fields.limits && renderTextInput('timeLimit', 'Time limit', 'milliseconds')}
          {fields.limits && renderTextInput('memoryLimit', 'Memory limit', 'kilobytes')}
          {fields.sourceFileFieldKeys && renderTextInput('sourceFileFieldKeys', 'Source file keys', 'comma-separated')}
          {renderSampleTestCases()}
          {renderTestGroups()}
          {fields.subtasks && renderSubtaskPoints()}
          {fields.customScorer && renderHelperSelect('customScorer', 'Custom scorer')}
          {fields.communicator && renderHelperSelect('communicator', 'Communicator')}
          {error && <Callout intent={Intent.DANGER}>{error}</Callout>}
          <ActionButtons justifyContent="end">
            <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={isSubmitting} />
          </ActionButtons>
        </Flex>
      </form>
    </Flex>
  );
}

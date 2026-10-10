import { Button, Checkbox, FormGroup, HTMLTable, InputGroup, Intent, Switch } from '@blueprintjs/core';
import { Plus, Trash } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import FormAceEditor from '../../../../components/forms/FormAceEditor/FormAceEditor';
import { FormInputValidation } from '../../../../components/forms/FormInputValidation/FormInputValidation';
import { FormRichTextArea } from '../../../../components/forms/FormRichTextArea/FormRichTextArea';
import { FormTextInput } from '../../../../components/forms/FormTextInput/FormTextInput';
import { getIntent } from '../../../../components/forms/meta';
import { Required, composeValidators } from '../../../../components/forms/validations';
import { ItemType } from '../../../../modules/api/problemBundle';
import { ProblemStatementManual } from '../ProblemStatementManual/ProblemStatementManual';

import './ProblemItemEditForm.scss';

const IsNumber = value => (value !== undefined && value !== '' && !isNaN(+value) ? undefined : 'Must be a number');

// An answer is matched against the whole regex, on the client and when it is graded.
const Regex = value => {
  try {
    new RegExp(`^(${value || ''})$`);
    return undefined;
  } catch {
    return 'Must be a valid regex';
  }
};

const Choices = choices => {
  const aliases = (choices || []).map(choice => choice.alias);
  if (aliases.some(alias => !alias)) {
    return 'Every choice must have an alias';
  }
  return new Set(aliases).size === aliases.length ? undefined : 'Every choice must have its own alias';
};

const metaField = {
  name: 'meta',
  label: 'Internal note',
};

const statementField = {
  name: 'statement',
  label: 'Statement',
  rows: 12,
  permissive: true,
};

const scoreField = {
  name: 'score',
  label: 'Score (points for a correct answer)',
  validate: IsNumber,
};

const penaltyField = {
  name: 'penalty',
  label: 'Penalty (points for a wrong answer)',
  validate: IsNumber,
};

const inputValidationRegexField = {
  name: 'inputValidationRegex',
  label: 'Answer format regex (matches the whole answer, e.g. [0-9]+,[0-9]+)',
  validate: composeValidators(Required, Regex),
};

const gradingRegexField = {
  name: 'gradingRegex',
  label: 'Correct answer regex (optional, matches the whole answer, e.g. (1,2)|(2,1))',
  validate: Regex,
};

const choicesField = {
  name: 'choices',
  validate: Choices,
};

function toFormValues({ meta, config }) {
  return {
    meta,
    statement: config.statement,
    score: config.score === undefined ? undefined : `${config.score}`,
    penalty: config.penalty === undefined ? undefined : `${config.penalty}`,
    inputValidationRegex: config.inputValidationRegex,
    gradingRegex: config.gradingRegex,
    choices: (config.choices || []).map(choice => ({ ...choice, isCorrect: !!choice.isCorrect })),
  };
}

function toConfig(type, values) {
  const statement = values.statement || '';
  switch (type) {
    case ItemType.MultipleChoice:
      return { statement, score: +values.score, penalty: +values.penalty, choices: values.choices };
    case ItemType.ShortAnswer:
      return {
        statement,
        score: +values.score,
        penalty: +values.penalty,
        inputValidationRegex: values.inputValidationRegex,
        gradingRegex: values.gradingRegex || undefined,
      };
    case ItemType.Essay:
      return { statement, score: +values.score };
    default:
      return { statement };
  }
}

function ChoicesInput({ input: { value, onChange }, meta }) {
  const choices = value || [];
  const setChoice = (idx, choice) => onChange(choices.map((c, i) => (i === idx ? { ...c, ...choice } : c)));

  return (
    <FormGroup label="Choices" helperText="The content of a choice is written as HTML." intent={getIntent(meta)}>
      <HTMLTable compact aria-label="Choices" className="problem-item-edit-form__choices">
        <thead>
          <tr>
            <th>Alias</th>
            <th>Content</th>
            <th>Correct?</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {choices.map((choice, idx) => (
            <tr key={idx}>
              <td className="problem-item-edit-form__choice-alias">
                <InputGroup
                  small
                  aria-label={`Alias of choice ${idx + 1}`}
                  value={choice.alias}
                  onChange={e => setChoice(idx, { alias: e.target.value })}
                />
              </td>
              <td>
                <InputGroup
                  small
                  fill
                  aria-label={`Content of choice ${idx + 1}`}
                  value={choice.content}
                  onChange={e => setChoice(idx, { content: e.target.value })}
                />
              </td>
              <td className="problem-item-edit-form__choice-action">
                <Checkbox
                  className="problem-item-edit-form__choice-correct"
                  aria-label={`Choice ${idx + 1} is correct`}
                  checked={choice.isCorrect}
                  onChange={e => setChoice(idx, { isCorrect: e.target.checked })}
                />
              </td>
              <td className="problem-item-edit-form__choice-action">
                <Button
                  small
                  minimal
                  intent={Intent.DANGER}
                  icon={<Trash />}
                  aria-label={`Remove choice ${idx + 1}`}
                  onClick={() => onChange(choices.filter((_, i) => i !== idx))}
                />
              </td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
      <ActionButtons>
        <Button
          small
          icon={<Plus />}
          text="New choice"
          onClick={() => onChange([...choices, { alias: '', content: '', isCorrect: false }])}
        />
      </ActionButtons>
      <FormInputValidation meta={meta} />
    </FormGroup>
  );
}

export default function ProblemItemEditForm({ item, onSubmit }) {
  const { type } = item;

  const [initialValues] = useState(() => toFormValues(item));
  const [isSourceMode, setIsSourceMode] = useState(false);

  const submit = values => onSubmit({ type, meta: values.meta || '', config: toConfig(type, values) });

  // The form renders again only on submit, so that editing one field leaves the rich text editor alone.
  return (
    <Form onSubmit={submit} initialValues={initialValues} subscription={{ submitting: true }}>
      {({ handleSubmit, submitting }) => (
        <Flex asChild flexDirection="column" gap={2}>
          <form className="problem-item-edit-form" onSubmit={handleSubmit}>
            <Field component={FormTextInput} {...metaField} />
            <Flex justifyContent="space-between" alignItems="center">
              <Switch
                label="Source mode"
                checked={isSourceMode}
                onChange={e => setIsSourceMode(e.target.checked)}
                style={{ marginBottom: 0 }}
              />
              <ProblemStatementManual />
            </Flex>
            {isSourceMode ? (
              <Field component={FormAceEditor} name={statementField.name} />
            ) : (
              <Field component={FormRichTextArea} {...statementField} />
            )}
            {type !== ItemType.Statement && <Field component={FormTextInput} {...scoreField} />}
            {(type === ItemType.MultipleChoice || type === ItemType.ShortAnswer) && (
              <Field component={FormTextInput} {...penaltyField} />
            )}
            {type === ItemType.ShortAnswer && <Field component={FormTextInput} {...inputValidationRegexField} />}
            {type === ItemType.ShortAnswer && <Field component={FormTextInput} {...gradingRegexField} />}
            {type === ItemType.MultipleChoice && <Field component={ChoicesInput} {...choicesField} />}
            <ActionButtons justifyContent="end">
              <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
            </ActionButtons>
          </form>
        </Flex>
      )}
    </Form>
  );
}

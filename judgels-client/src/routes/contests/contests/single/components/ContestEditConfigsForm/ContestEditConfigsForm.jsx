import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../../../components/ActionButtons/ActionButtons';
import { FormCheckbox } from '../../../../../../components/forms/FormCheckbox/FormCheckbox';
import { FormRichTextArea } from '../../../../../../components/forms/FormRichTextArea/FormRichTextArea';
import { FormTableCheckbox } from '../../../../../../components/forms/FormTableCheckbox/FormTableCheckbox';
import { FormTableInput } from '../../../../../../components/forms/FormTableInput/FormTableInput';
import { FormTableTextInput } from '../../../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { NonnegativeNumber, Required, composeValidators } from '../../../../../../components/forms/validations';
import { getGradingLanguageName, gradingLanguages } from '../../../../../../modules/api/gabriel/language.js';

import './ContestEditConfigsForm.scss';

export default function ContestEditConfigsForm({ onSubmit, initialValues, config, onCancel }) {
  const renderTrocStyleForm = ({ trocAllowAllLanguages }) => {
    const allowedLanguageField = {
      label: 'Allowed languages',
      meta: {},
    };
    const allowAllLanguagesField = {
      name: 'trocAllowAllLanguages',
      label: '(all)',
    };
    const allowedLanguageFields = gradingLanguages.map(lang => ({
      name: 'trocAllowedLanguages.' + lang,
      label: getGradingLanguageName(lang),
      small: true,
    }));

    const wrongSubmissionPenaltyField = {
      name: 'trocWrongSubmissionPenalty',
      label: 'Wrong submission penalty',
      validate: composeValidators(Required, NonnegativeNumber),
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>TROC style config</h4>
        <HTMLTable striped>
          <tbody>
            <FormTableInput {...allowedLanguageField}>
              <Field component={FormCheckbox} {...allowAllLanguagesField} />
              {!trocAllowAllLanguages &&
                allowedLanguageFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
            </FormTableInput>
            <Field component={FormTableTextInput} {...wrongSubmissionPenaltyField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  const renderIcpcStyleForm = ({ icpcAllowAllLanguages }) => {
    const allowedLanguageField = {
      label: 'Allowed languages',
      meta: {},
    };
    const allowAllLanguagesField = {
      name: 'icpcAllowAllLanguages',
      label: '(all)',
    };
    const allowedLanguageFields = gradingLanguages.map(lang => ({
      name: 'icpcAllowedLanguages.' + lang,
      label: getGradingLanguageName(lang),
      small: true,
    }));

    const wrongSubmissionPenaltyField = {
      name: 'icpcWrongSubmissionPenalty',
      label: 'Wrong submission penalty',
      validate: composeValidators(Required, NonnegativeNumber),
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>ICPC style config</h4>
        <HTMLTable striped>
          <tbody>
            <FormTableInput {...allowedLanguageField}>
              <Field component={FormCheckbox} {...allowAllLanguagesField} />
              {!icpcAllowAllLanguages &&
                allowedLanguageFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
            </FormTableInput>
            <Field component={FormTableTextInput} {...wrongSubmissionPenaltyField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  const renderIoiStyleForm = ({ ioiAllowAllLanguages }) => {
    const allowedLanguageField = {
      label: 'Allowed languages',
      meta: {},
    };
    const allowAllLanguagesField = {
      name: 'ioiAllowAllLanguages',
      label: '(all)',
    };
    const allowedLanguageFields = gradingLanguages.map(lang => ({
      name: 'ioiAllowedLanguages.' + lang,
      label: getGradingLanguageName(lang),
      small: true,
    }));

    const usingLastAffectingPenaltyField = {
      name: 'ioiUsingLastAffectingPenalty',
      label: 'Using last affecting penalty?',
      keyClassName: 'contest-edit-configs-form__key',
    };

    const usingMaxScorePerSubtaskField = {
      name: 'ioiUsingMaxScorePerSubtask',
      label: 'Using max score per subtask?',
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>IOI style config</h4>
        <HTMLTable striped>
          <tbody>
            <FormTableInput {...allowedLanguageField}>
              <Field component={FormCheckbox} {...allowAllLanguagesField} />
              {!ioiAllowAllLanguages &&
                allowedLanguageFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
            </FormTableInput>
            <Field component={FormTableCheckbox} {...usingLastAffectingPenaltyField} />
            <Field component={FormTableCheckbox} {...usingMaxScorePerSubtaskField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  const renderGcjStyleForm = ({ gcjAllowAllLanguages }) => {
    const allowedLanguageField = {
      label: 'Allowed languages',
      meta: {},
    };
    const allowAllLanguagesField = {
      name: 'gcjAllowAllLanguages',
      label: '(all)',
    };
    const allowedLanguageFields = gradingLanguages.map(lang => ({
      name: 'gcjAllowedLanguages.' + lang,
      label: getGradingLanguageName(lang),
      small: true,
    }));

    const wrongSubmissionPenaltyField = {
      name: 'gcjWrongSubmissionPenalty',
      label: 'Wrong submission penalty',
      validate: composeValidators(Required, NonnegativeNumber),
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>GCJ style config</h4>
        <HTMLTable striped>
          <tbody>
            <FormTableInput {...allowedLanguageField}>
              <Field component={FormCheckbox} {...allowAllLanguagesField} />
              {!gcjAllowAllLanguages &&
                allowedLanguageFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
            </FormTableInput>
            <Field component={FormTableTextInput} {...wrongSubmissionPenaltyField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  const renderClarificationTimeLimitForm = () => {
    const clarificationTimeLimitDurationField = {
      name: 'clarificationTimeLimitDuration',
      label: 'Clarification duration',
      inputHelper: 'Since contest start time. Example: 2h 30m',
      validate: Required,
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>Clarification time limit config</h4>
        <HTMLTable striped>
          <tbody>
            <Field component={FormTableTextInput} {...clarificationTimeLimitDurationField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  const renderDivisionForm = () => {
    const divisionDivisionField = {
      name: 'divisionDivision',
      label: 'Division',
      validate: Required,
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>Division config</h4>
        <HTMLTable striped>
          <tbody>
            <Field component={FormTableTextInput} {...divisionDivisionField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  const renderEditorialForm = () => {
    const editorialPrefaceField = {
      name: 'editorialPreface',
      label: 'Preface',
      rows: 15,
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>Editorial config</h4>
        <Field component={FormRichTextArea} {...editorialPrefaceField} />
      </div>
    );
  };

  const renderScoreboardForm = () => {
    const scoreboardIsIncognitoField = {
      name: 'scoreboardIsIncognito',
      label: 'Incognito scoreboard?',
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>Scoreboard config</h4>
        <HTMLTable striped>
          <tbody>
            <Field component={FormTableCheckbox} {...scoreboardIsIncognitoField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  const renderFrozenScoreboardForm = () => {
    const frozenScoreboardFreezeTimeField = {
      name: 'frozenScoreboardFreezeTime',
      label: 'Freeze time',
      inputHelper: 'Before contest end time. Example: 2h 30m',
      validate: Required,
      keyClassName: 'contest-edit-configs-form__key',
    };

    const frozenScoreboardIsOfficialAllowedField = {
      name: 'frozenScoreboardIsOfficialAllowed',
      label: 'Is now unfrozen?',
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>Frozen scoreboard config</h4>
        <HTMLTable striped>
          <tbody>
            <Field component={FormTableTextInput} {...frozenScoreboardFreezeTimeField} />
            <Field component={FormTableCheckbox} {...frozenScoreboardIsOfficialAllowedField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  const renderMergedScoreboardForm = () => {
    const mergedScoreboardPreviousContestJidField = {
      name: 'mergedScoreboardPreviousContestJid',
      label: 'Previous contest JID',
      validate: Required,
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>Merged scoreboard config</h4>
        <HTMLTable striped>
          <tbody>
            <Field component={FormTableTextInput} {...mergedScoreboardPreviousContestJidField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  const renderExternalScoreboardForm = () => {
    const externalScoreboardReceiverUrlField = {
      name: 'externalScoreboardReceiverUrl',
      label: 'Receiver URL',
      validate: Required,
      keyClassName: 'contest-edit-configs-form__key',
    };

    const externalScoreboardReceiverSecretField = {
      name: 'externalScoreboardReceiverSecret',
      label: 'Receiver secret',
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>External scoreboard config</h4>
        <HTMLTable striped>
          <tbody>
            <Field component={FormTableTextInput} {...externalScoreboardReceiverUrlField} />
            <Field component={FormTableTextInput} {...externalScoreboardReceiverSecretField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  const renderVirtualForm = () => {
    const virtualDurationField = {
      name: 'virtualDuration',
      label: 'Virtual contest duration',
      inputHelper: 'Example: 2h 30m',
      validate: Required,
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>Virtual contest config</h4>
        <HTMLTable striped>
          <tbody>
            <Field component={FormTableTextInput} {...virtualDurationField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, values, submitting }) => (
        <form className="contest-edit-dialog__content" onSubmit={handleSubmit}>
          {config.trocStyle && renderTrocStyleForm(values)}
          {config.icpcStyle && renderIcpcStyleForm(values)}
          {config.ioiStyle && renderIoiStyleForm(values)}
          {config.gcjStyle && renderGcjStyleForm(values)}
          {config.clarificationTimeLimit && renderClarificationTimeLimitForm()}
          {config.division && renderDivisionForm()}
          {renderScoreboardForm()}
          {config.frozenScoreboard && renderFrozenScoreboardForm()}
          {config.mergedScoreboard && renderMergedScoreboardForm()}
          {config.externalScoreboard && renderExternalScoreboardForm()}
          {config.virtual && renderVirtualForm()}
          {config.editorial && renderEditorialForm()}

          <hr />
          <ActionButtons>
            <Button text="Cancel" disabled={submitting} onClick={onCancel} />
            <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
          </ActionButtons>
        </form>
      )}
    </Form>
  );
}

import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Component } from 'react';
import { Field, reduxForm } from 'redux-form';

import {
  allLanguagesAllowed,
  getGradingLanguageName,
  gradingLanguages,
} from '../../../../../../modules/api/gabriel/language.js';
import { ActionButtons } from '../../../../../../components/ActionButtons/ActionButtons';
import { FormTableInput } from '../../../../../../components/forms/FormTableInput/FormTableInput';
import { FormCheckbox } from '../../../../../../components/forms/FormCheckbox/FormCheckbox';
import { FormTableCheckbox } from '../../../../../../components/forms/FormTableCheckbox/FormTableCheckbox';
import { FormTableTextInput } from '../../../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { NonnegativeNumber, Required } from '../../../../../../components/forms/validations';

import './ContestEditConfigsForm.css';

class ContestEditConfigsForm extends Component {
  state;

  constructor(props) {
    super(props);

    const { trocStyle, icpcStyle, ioiStyle, gcjStyle } = props.config;
    const allowAllLanguages =
      (!!trocStyle && allLanguagesAllowed(trocStyle.languageRestriction)) ||
      (!!icpcStyle && allLanguagesAllowed(icpcStyle.languageRestriction)) ||
      (!!ioiStyle && allLanguagesAllowed(ioiStyle.languageRestriction)) ||
      (!!gcjStyle && allLanguagesAllowed(gcjStyle.languageRestriction));

    this.state = { allowAllLanguages };
  }

  render() {
    const { handleSubmit, submitting, config, onCancel } = this.props;
    return (
      <form className="contest-edit-dialog__content" onSubmit={handleSubmit}>
        {config.trocStyle && this.renderTrocStyleForm()}
        {config.icpcStyle && this.renderIcpcStyleForm()}
        {config.ioiStyle && this.renderIoiStyleForm()}
        {config.gcjStyle && this.renderGcjStyleForm()}
        {config.clarificationTimeLimit && this.renderClarificationTimeLimitForm()}
        {config.division && this.renderDivisionForm()}
        {this.renderScoreboardForm()}
        {config.frozenScoreboard && this.renderFrozenScoreboardForm()}
        {config.externalScoreboard && this.renderExternalScoreboardForm()}
        {config.virtual && this.renderVirtualForm()}

        <hr />
        <ActionButtons>
          <Button text="Cancel" disabled={submitting} onClick={onCancel} />
          <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
        </ActionButtons>
      </form>
    );
  }

  toggleAllowAllLanguagesCheckbox = (e, checked) => {
    this.setState({ allowAllLanguages: checked });
  };

  renderTrocStyleForm = () => {
    const allowedLanguageField = {
      label: 'Allowed languages',
      meta: {},
    };
    const allowAllLanguagesField = {
      name: 'trocAllowAllLanguages',
      label: '(all)',
      onChange: this.toggleAllowAllLanguagesCheckbox,
    };
    const allowedLanguageFields = gradingLanguages.map(lang => ({
      name: 'trocAllowedLanguages.' + lang,
      label: getGradingLanguageName(lang),
      small: true,
    }));

    const wrongSubmissionPenaltyField = {
      name: 'trocWrongSubmissionPenalty',
      label: 'Wrong submission penalty',
      validate: [Required, NonnegativeNumber],
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>TROC style config</h4>
        <HTMLTable striped>
          <tbody>
            <FormTableInput {...allowedLanguageField}>
              <Field component={FormCheckbox} {...allowAllLanguagesField} />
              {!this.state.allowAllLanguages &&
                allowedLanguageFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
            </FormTableInput>
            <Field component={FormTableTextInput} {...wrongSubmissionPenaltyField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  renderIcpcStyleForm = () => {
    const allowedLanguageField = {
      label: 'Allowed languages',
      meta: {},
    };
    const allowAllLanguagesField = {
      name: 'icpcAllowAllLanguages',
      label: '(all)',
      onChange: this.toggleAllowAllLanguagesCheckbox,
    };
    const allowedLanguageFields = gradingLanguages.map(lang => ({
      name: 'icpcAllowedLanguages.' + lang,
      label: getGradingLanguageName(lang),
      small: true,
    }));

    const wrongSubmissionPenaltyField = {
      name: 'icpcWrongSubmissionPenalty',
      label: 'Wrong submission penalty',
      validate: [Required, NonnegativeNumber],
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>ICPC style config</h4>
        <HTMLTable striped>
          <tbody>
            <FormTableInput {...allowedLanguageField}>
              <Field component={FormCheckbox} {...allowAllLanguagesField} />
              {!this.state.allowAllLanguages &&
                allowedLanguageFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
            </FormTableInput>
            <Field component={FormTableTextInput} {...wrongSubmissionPenaltyField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  renderIoiStyleForm = () => {
    const allowedLanguageField = {
      label: 'Allowed languages',
      meta: {},
    };
    const allowAllLanguagesField = {
      name: 'ioiAllowAllLanguages',
      label: '(all)',
      onChange: this.toggleAllowAllLanguagesCheckbox,
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
              {!this.state.allowAllLanguages &&
                allowedLanguageFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
            </FormTableInput>
            <Field component={FormTableCheckbox} {...usingLastAffectingPenaltyField} />
            <Field component={FormTableCheckbox} {...usingMaxScorePerSubtaskField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  renderGcjStyleForm = () => {
    const allowedLanguageField = {
      label: 'Allowed languages',
      meta: {},
    };
    const allowAllLanguagesField = {
      name: 'gcjAllowAllLanguages',
      label: '(all)',
      onChange: this.toggleAllowAllLanguagesCheckbox,
    };
    const allowedLanguageFields = gradingLanguages.map(lang => ({
      name: 'gcjAllowedLanguages.' + lang,
      label: getGradingLanguageName(lang),
      small: true,
    }));

    const wrongSubmissionPenaltyField = {
      name: 'gcjWrongSubmissionPenalty',
      label: 'Wrong submission penalty',
      validate: [Required, NonnegativeNumber],
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>GCJ style config</h4>
        <HTMLTable striped>
          <tbody>
            <FormTableInput {...allowedLanguageField}>
              <Field component={FormCheckbox} {...allowAllLanguagesField} />
              {!this.state.allowAllLanguages &&
                allowedLanguageFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
            </FormTableInput>
            <Field component={FormTableTextInput} {...wrongSubmissionPenaltyField} />
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  renderClarificationTimeLimitForm = () => {
    const clarificationTimeLimitDurationField = {
      name: 'clarificationTimeLimitDuration',
      label: 'Clarification duration',
      inputHelper: 'Since contest start time. Example: 2h 30m',
      validate: [Required],
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

  renderDivisionForm = () => {
    const divisionDivisionField = {
      name: 'divisionDivision',
      label: 'Division',
      validate: [Required],
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

  renderScoreboardForm = () => {
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

  renderFrozenScoreboardForm = () => {
    const frozenScoreboardFreezeTimeField = {
      name: 'frozenScoreboardFreezeTime',
      label: 'Freeze time',
      inputHelper: 'Before contest end time. Example: 2h 30m',
      validate: [Required],
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

  renderExternalScoreboardForm = () => {
    const externalScoreboardReceiverUrlField = {
      name: 'externalScoreboardReceiverUrl',
      label: 'Receiver URL',
      validate: [Required],
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

  renderVirtualForm = () => {
    const virtualDurationField = {
      name: 'virtualDuration',
      label: 'Virtual contest duration',
      inputHelper: 'Example: 2h 30m',
      validate: [Required],
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
}

export default reduxForm({
  form: 'contest-edit-configs',
  touchOnBlur: false,
})(ContestEditConfigsForm);

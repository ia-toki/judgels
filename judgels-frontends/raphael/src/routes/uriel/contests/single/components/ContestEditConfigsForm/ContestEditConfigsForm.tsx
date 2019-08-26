import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { allLanguagesAllowed, getGradingLanguageName, gradingLanguages } from 'modules/api/gabriel/language';
import { ContestModulesConfig } from 'modules/api/uriel/contestModule';
import { ActionButtons } from 'components/ActionButtons/ActionButtons';
import { FormTableInput } from 'components/forms/FormTableInput/FormTableInput';
import { FormCheckbox } from 'components/forms/FormCheckbox/FormCheckbox';
import { FormTableCheckbox } from 'components/forms/FormTableCheckbox/FormTableCheckbox';
import { FormTableTextInput } from 'components/forms/FormTableTextInput/FormTableTextInput';
import { NonnegativeNumber, Required } from 'components/forms/validations';

import './ContestEditConfigsForm.css';

export interface ContestEditConfigsFormData {
  icpcAllowAllLanguages?: boolean;
  icpcAllowedLanguages?: { [key: string]: string };
  icpcWrongSubmissionPenalty?: string;

  ioiAllowAllLanguages?: boolean;
  ioiAllowedLanguages?: { [key: string]: string };
  ioiUsingLastAffectingPenalty?: boolean;
  ioiUsingMaxScorePerSubtask?: boolean;

  gcjAllowAllLanguages?: boolean;
  gcjAllowedLanguages?: { [key: string]: string };
  gcjWrongSubmissionPenalty?: string;

  scoreboardIsIncognito: boolean;

  clarificationTimeLimitDuration?: string;

  divisionDivision?: number;

  externalScoreboardReceiverUrl?: string;
  externalScoreboardReceiverSecret?: string;

  frozenScoreboardFreezeTime?: string;
  frozenScoreboardIsOfficialAllowed?: boolean;

  virtualDuration?: string;
}

export interface ContestEditConfigsFormProps extends InjectedFormProps<ContestEditConfigsFormData> {
  config: ContestModulesConfig;
  onCancel: () => void;
}

interface ContestEditConfigFormState {
  allowAllLanguages: boolean;
}

class ContestEditConfigsForm extends React.Component<ContestEditConfigsFormProps, ContestEditConfigFormState> {
  state: ContestEditConfigFormState;

  constructor(props: ContestEditConfigsFormProps) {
    super(props);

    const { icpcStyle, ioiStyle, gcjStyle } = props.config;
    const allowAllLanguages =
      (!!icpcStyle && allLanguagesAllowed(icpcStyle.languageRestriction)) ||
      (!!ioiStyle && allLanguagesAllowed(ioiStyle.languageRestriction)) ||
      (!!gcjStyle && allLanguagesAllowed(gcjStyle.languageRestriction));

    this.state = { allowAllLanguages };
  }

  render() {
    const { config } = this.props;
    return (
      <form className="contest-edit-dialog__content" onSubmit={this.props.handleSubmit}>
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
          <Button text="Cancel" disabled={this.props.submitting} onClick={this.props.onCancel} />
          <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={this.props.submitting} />
        </ActionButtons>
      </form>
    );
  }

  private toggleAllowAllLanguagesCheckbox = (e, checked) => {
    this.setState({ allowAllLanguages: checked });
  };

  private renderIcpcStyleForm = () => {
    const allowedLanguageField: any = {
      label: 'Allowed languages',
      meta: {},
    };
    const allowAllLanguagesField: any = {
      name: 'icpcAllowAllLanguages',
      label: '(all)',
      onChange: this.toggleAllowAllLanguagesCheckbox,
    };
    const allowedLanguageFields = gradingLanguages.map(
      lang =>
        ({
          name: 'icpcAllowedLanguages.' + lang,
          label: getGradingLanguageName(lang),
          small: true,
        } as any)
    );

    const wrongSubmissionPenaltyField: any = {
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

  private renderIoiStyleForm = () => {
    const allowedLanguageField: any = {
      label: 'Allowed languages',
      meta: {},
    };
    const allowAllLanguagesField: any = {
      name: 'ioiAllowAllLanguages',
      label: '(all)',
      onChange: this.toggleAllowAllLanguagesCheckbox,
    };
    const allowedLanguageFields = gradingLanguages.map(
      lang =>
        ({
          name: 'ioiAllowedLanguages.' + lang,
          label: getGradingLanguageName(lang),
          small: true,
        } as any)
    );

    const usingLastAffectingPenaltyField: any = {
      name: 'ioiUsingLastAffectingPenalty',
      label: 'Using last affecting penalty?',
      keyClassName: 'contest-edit-configs-form__key',
    };

    const usingMaxScorePerSubtaskField: any = {
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

  private renderGcjStyleForm = () => {
    const allowedLanguageField: any = {
      label: 'Allowed languages',
      meta: {},
    };
    const allowAllLanguagesField: any = {
      name: 'gcjAllowAllLanguages',
      label: '(all)',
      onChange: this.toggleAllowAllLanguagesCheckbox,
    };
    const allowedLanguageFields = gradingLanguages.map(
      lang =>
        ({
          name: 'gcjAllowedLanguages.' + lang,
          label: getGradingLanguageName(lang),
          small: true,
        } as any)
    );

    const wrongSubmissionPenaltyField: any = {
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

  private renderClarificationTimeLimitForm = () => {
    const clarificationTimeLimitDurationField: any = {
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

  private renderDivisionForm = () => {
    const divisionDivisionField: any = {
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

  private renderScoreboardForm = () => {
    const scoreboardIsIncognitoField: any = {
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

  private renderFrozenScoreboardForm = () => {
    const frozenScoreboardFreezeTimeField: any = {
      name: 'frozenScoreboardFreezeTime',
      label: 'Freeze time',
      inputHelper: 'Before contest end time. Example: 2h 30m',
      validate: [Required],
      keyClassName: 'contest-edit-configs-form__key',
    };

    const frozenScoreboardIsOfficialAllowedField: any = {
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

  private renderExternalScoreboardForm = () => {
    const externalScoreboardReceiverUrlField: any = {
      name: 'externalScoreboardReceiverUrl',
      label: 'Receiver URL',
      validate: [Required],
      keyClassName: 'contest-edit-configs-form__key',
    };

    const externalScoreboardReceiverSecretField: any = {
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

  private renderVirtualForm = () => {
    const virtualDurationField: any = {
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

export default reduxForm<ContestEditConfigsFormData>({
  form: 'contest-edit-configs',
  touchOnBlur: false,
})(ContestEditConfigsForm);

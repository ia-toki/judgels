import { Button, Intent } from '@blueprintjs/core';
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

  scoreboardIsIncognito: boolean;

  clarificationTimeLimitDuration?: string;

  delayedGradingDuration?: string;

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

    const { icpcStyle, ioiStyle } = props.config;
    const allowAllLanguages =
      (!!icpcStyle && allLanguagesAllowed(icpcStyle.languageRestriction)) ||
      (!!ioiStyle && allLanguagesAllowed(ioiStyle.languageRestriction));

    this.state = { allowAllLanguages };
  }

  render() {
    const { config } = this.props;
    return (
      <form className="contest-edit-dialog__content" onSubmit={this.props.handleSubmit}>
        {config.icpcStyle && this.renderIcpcStyleForm()}
        {config.ioiStyle && this.renderIoiStyleForm()}
        {config.clarificationTimeLimit && this.renderClarificationTimeLimitForm()}
        {config.delayedGrading && this.renderDelayedGradingForm()}
        {this.renderScoreboardForm()}
        {config.frozenScoreboard && this.renderFrozenScoreboardForm()}
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
        <table className="bp3-html-table bp3-html-table-striped">
          <tbody>
            <FormTableInput {...allowedLanguageField}>
              <Field component={FormCheckbox} {...allowAllLanguagesField} />
              {!this.state.allowAllLanguages &&
                allowedLanguageFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
            </FormTableInput>
            <Field component={FormTableTextInput} {...wrongSubmissionPenaltyField} />
          </tbody>
        </table>
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

    return (
      <div className="contest-edit-configs-form__config">
        <h4>IOI style config</h4>
        <table className="bp3-html-table bp3-html-table-striped">
          <tbody>
            <FormTableInput {...allowedLanguageField}>
              <Field component={FormCheckbox} {...allowAllLanguagesField} />
              {!this.state.allowAllLanguages &&
                allowedLanguageFields.map(f => <Field key={f.name} component={FormCheckbox} {...f} />)}
            </FormTableInput>
            <Field component={FormTableCheckbox} {...usingLastAffectingPenaltyField} />
          </tbody>
        </table>
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
        <table className="bp3-html-table bp3-html-table-striped">
          <tbody>
            <Field component={FormTableTextInput} {...clarificationTimeLimitDurationField} />
          </tbody>
        </table>
      </div>
    );
  };

  private renderDelayedGradingForm = () => {
    const delayedGradingDurationField: any = {
      name: 'delayedGradingDuration',
      label: 'Delay duration',
      inputHelper: 'Example: 2h 30m',
      validate: [Required],
      keyClassName: 'contest-edit-configs-form__key',
    };

    return (
      <div className="contest-edit-configs-form__config">
        <h4>Delayed grading config</h4>
        <table className="bp3-html-table bp3-html-table-striped">
          <tbody>
            <Field component={FormTableTextInput} {...delayedGradingDurationField} />
          </tbody>
        </table>
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
        <table className="bp3-html-table bp3-html-table-striped">
          <tbody>
            <Field component={FormTableCheckbox} {...scoreboardIsIncognitoField} />
          </tbody>
        </table>
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
        <table className="bp3-html-table bp3-html-table-striped">
          <tbody>
            <Field component={FormTableTextInput} {...frozenScoreboardFreezeTimeField} />
            <Field component={FormTableCheckbox} {...frozenScoreboardIsOfficialAllowedField} />
          </tbody>
        </table>
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
        <table className="bp3-html-table bp3-html-table-striped">
          <tbody>
            <Field component={FormTableTextInput} {...virtualDurationField} />
          </tbody>
        </table>
      </div>
    );
  };
}

export default reduxForm<ContestEditConfigsFormData>({
  form: 'contest-edit-configs',
  touchOnBlur: false,
})(ContestEditConfigsForm);

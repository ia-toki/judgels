import { Button, Callout, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { FormTableFileInput } from 'components/forms/FormTableFileInput/FormTableFileInput';
import { FormTableSelect2 } from 'components/forms/FormTableSelect2/FormTableSelect2';
import {
  CompatibleFilenameExtensionForGradingLanguage,
  MaxFileSize300KB,
  Required,
} from 'components/forms/validations';
import { GradingEngineCode } from 'modules/api/gabriel/engine';
import { gradingLanguageNamesMap } from 'modules/api/gabriel/language';

import './ProgrammingProblemSubmissionForm.css';

export interface ProgrammingProblemSubmissionFormData {
  gradingLanguage: string;
  sourceFiles: { [key: string]: File };
}

interface ProgrammingProblemSubmissionFormProps extends InjectedFormProps<ProgrammingProblemSubmissionFormData> {
  sourceKeys: { [key: string]: string };
  gradingEngine: string;
  gradingLanguages: string[];
  submissionWarning?: string;
}

class ProgrammingProblemSubmissionForm extends React.PureComponent<ProgrammingProblemSubmissionFormProps> {
  render() {
    return (
      <form onSubmit={this.props.handleSubmit}>
        {this.renderWarning()}
        <table className="problem-submission-form__table">
          <tbody>
            {this.renderSourceFields()}
            {this.renderGradingLanguageFields()}
          </tbody>
        </table>
        <Button type="submit" text="Submit" intent={Intent.PRIMARY} loading={this.props.submitting} />
      </form>
    );
  }

  private renderWarning = () => {
    return (
      this.props.submissionWarning && (
        <Callout icon="warning-sign" className="problem-submission-form__warning" data-key="submission-warning">
          {this.props.submissionWarning}
        </Callout>
      )
    );
  };

  private renderSourceFields = () => {
    const { sourceKeys } = this.props;
    return Object.keys(sourceKeys)
      .sort()
      .map(key => {
        const field: any = {
          name: 'sourceFiles.' + key,
          label: sourceKeys[key],
          validate: [Required, MaxFileSize300KB, CompatibleFilenameExtensionForGradingLanguage],
        };
        return <Field key={key} component={FormTableFileInput} {...field} />;
      });
  };

  private renderGradingLanguageFields = () => {
    const { gradingEngine, gradingLanguages } = this.props;
    if (gradingEngine === GradingEngineCode.OutputOnly) {
      return null;
    }

    const field: any = {
      name: 'gradingLanguage',
      label: 'Language',
      validate: [Required],
      optionValues: gradingLanguages,
      optionNamesMap: gradingLanguageNamesMap,
    };

    return <Field component={FormTableSelect2} {...field} />;
  };
}

export default reduxForm<ProgrammingProblemSubmissionFormData>({
  form: 'problem-submission',
  touchOnBlur: false,
})(ProgrammingProblemSubmissionForm);

import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { FormTableFileInput } from '../forms/FormTableFileInput/FormTableFileInput';
import { FormTableSelect2 } from '../forms/FormTableSelect2/FormTableSelect2';
import { Required } from '../forms/validations';
import { ProblemSubmissionConfiguration } from '../../modules/api/sandalphon/problem';
import { GradingEngineCode } from '../../modules/api/gabriel/engine';
import { gradingLanguages, gradingLanguageNamesMap } from '../../modules/api/gabriel/language';

import './ProblemSubmissionForm.css';

export interface ProblemSubmissionFormData {
  gradingLanguage: string;
  sourceFiles: { [key: string]: File };
}

export interface ProblemSubmissionFormProps extends InjectedFormProps<ProblemSubmissionFormData> {
  config: ProblemSubmissionConfiguration;
}

class ProblemSubmissionForm extends React.Component<ProblemSubmissionFormProps> {
  render() {
    return (
      <form onSubmit={this.props.handleSubmit}>
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

  private renderSourceFields = () => {
    const { sourceKeys } = this.props.config;
    return Object.keys(sourceKeys)
      .sort()
      .map(key => {
        const field: any = {
          name: 'sourceFiles.' + key,
          label: sourceKeys[key],
          validate: [Required],
        };
        return <Field key={key} component={FormTableFileInput} {...field} />;
      });
  };

  private renderGradingLanguageFields = () => {
    const { gradingEngine } = this.props.config;
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

export default reduxForm<ProblemSubmissionFormData>({
  form: 'contest-problem-submission',
  initialValues: { gradingLanguage: 'Cpp11' },
} as any)(ProblemSubmissionForm);

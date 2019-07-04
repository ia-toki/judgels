import { Button, Callout, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { isOutputOnly } from 'modules/api/gabriel/engine';
import { FormTableFileInput } from 'components/forms/FormTableFileInput/FormTableFileInput';
import { FormTableSelect2 } from 'components/forms/FormTableSelect2/FormTableSelect2';
import {
  CompatibleFilenameExtensionForGradingLanguage,
  MaxFileSize300KB,
  MaxFileSize10MB,
  Required,
} from 'components/forms/validations';
import { gradingLanguageNamesMap } from 'modules/api/gabriel/language';

import './ProblemSubmissionForm.css';

export interface ProblemSubmissionFormData {
  gradingLanguage: string;
  sourceFiles: { [key: string]: File };
}

interface ProblemSubmissionFormProps extends InjectedFormProps<ProblemSubmissionFormData> {
  sourceKeys: { [key: string]: string };
  gradingEngine: string;
  gradingLanguages: string[];
  submissionWarning?: string;
}

class ProblemSubmissionForm extends React.PureComponent<ProblemSubmissionFormProps> {
  render() {
    return (
      <form onSubmit={this.props.handleSubmit}>
        {this.renderWarning()}
        <table className="programming-problem-submission-form__table">
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
        <Callout
          icon="warning-sign"
          className="programming-problem-submission-form__warning"
          data-key="submission-warning"
        >
          {this.props.submissionWarning}
        </Callout>
      )
    );
  };

  private renderSourceFields = () => {
    const { gradingEngine, sourceKeys } = this.props;
    let maxFileSize;
    if (isOutputOnly(gradingEngine)) {
      maxFileSize = MaxFileSize10MB;
    } else {
      maxFileSize = MaxFileSize300KB;
    }

    return Object.keys(sourceKeys)
      .sort()
      .map(key => {
        const field: any = {
          name: 'sourceFiles.' + key,
          label: sourceKeys[key],
          validate: [Required, maxFileSize, CompatibleFilenameExtensionForGradingLanguage],
        };
        return <Field key={key} component={FormTableFileInput} {...field} />;
      });
  };

  private renderGradingLanguageFields = () => {
    const { gradingEngine, gradingLanguages } = this.props;
    if (isOutputOnly(gradingEngine)) {
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
  form: 'problem-submission',
  touchOnBlur: false,
})(ProblemSubmissionForm);

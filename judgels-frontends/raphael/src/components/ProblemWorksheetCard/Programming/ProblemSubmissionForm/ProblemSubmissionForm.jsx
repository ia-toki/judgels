import { Button, Callout, Intent } from '@blueprintjs/core';
import { Field, reduxForm } from 'redux-form';

import { isOutputOnly } from '../../../../modules/api/gabriel/engine';
import { FormTableFileInput } from '../../../forms/FormTableFileInput/FormTableFileInput';
import { FormTableSelect2 } from '../../../forms/FormTableSelect2/FormTableSelect2';
import {
  CompatibleFilenameExtensionForGradingLanguage,
  MaxFileSize300KB,
  MaxFileSize10MB,
  Required,
} from '../../../forms/validations';
import { gradingLanguageNamesMap } from '../../../../modules/api/gabriel/language.js';

import './ProblemSubmissionForm.scss';

function ProblemSubmissionForm({
  handleSubmit,
  submitting,
  sourceKeys,
  gradingEngine,
  gradingLanguages,
  submissionWarning,
}) {
  const renderWarning = () => {
    return (
      submissionWarning && (
        <Callout
          icon="warning-sign"
          className="programming-problem-submission-form__warning"
          data-key="submission-warning"
        >
          {submissionWarning}
        </Callout>
      )
    );
  };

  const renderSourceFields = () => {
    let maxFileSize;
    if (isOutputOnly(gradingEngine)) {
      maxFileSize = MaxFileSize10MB;
    } else {
      maxFileSize = MaxFileSize300KB;
    }

    return Object.keys(sourceKeys)
      .sort()
      .map(key => {
        const field = {
          name: 'sourceFiles.' + key,
          label: sourceKeys[key],
          validate: [Required, maxFileSize, CompatibleFilenameExtensionForGradingLanguage],
        };
        return <Field key={key} component={FormTableFileInput} {...field} />;
      });
  };

  const renderGradingLanguageFields = () => {
    if (isOutputOnly(gradingEngine)) {
      return null;
    }

    const field = {
      name: 'gradingLanguage',
      label: 'Language',
      validate: [Required],
      optionValues: gradingLanguages,
      optionNamesMap: gradingLanguageNamesMap,
    };

    return <Field component={FormTableSelect2} {...field} />;
  };

  return (
    <form onSubmit={handleSubmit}>
      {renderWarning()}
      <table className="programming-problem-submission-form__table">
        <tbody>
          {renderSourceFields()}
          {renderGradingLanguageFields()}
        </tbody>
      </table>
      <Button type="submit" text="Submit" intent={Intent.PRIMARY} loading={submitting} />
    </form>
  );
}

export default reduxForm({
  form: 'problem-submission',
  touchOnBlur: false,
})(ProblemSubmissionForm);

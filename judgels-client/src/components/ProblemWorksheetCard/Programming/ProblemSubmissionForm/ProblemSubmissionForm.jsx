import { Button, Callout, Intent } from '@blueprintjs/core';
import { WarningSign } from '@blueprintjs/icons';
import { Field, Form } from 'react-final-form';

import { isOutputOnly } from '../../../../modules/api/gabriel/engine';
import { gradingLanguageNamesMap } from '../../../../modules/api/gabriel/language.js';
import FormAceEditor from '../../../forms/FormAceEditor/FormAceEditor.jsx';
import { FormTableFileInput } from '../../../forms/FormTableFileInput/FormTableFileInput';
import { FormTableSelect2 } from '../../../forms/FormTableSelect2/FormTableSelect2';
import {
  CompatibleFilenameExtensionForGradingLanguage,
  MaxFileSize10MB,
  MaxFileSize300KB,
  Required,
  composeValidators,
} from '../../../forms/validations';

import './ProblemSubmissionForm.scss';

export default function ProblemSubmissionForm({
  onSubmit,
  initialValues,
  sourceKeys,
  gradingEngine,
  gradingLanguages,
  submissionWarning,
}) {
  const renderWarning = () => {
    return (
      submissionWarning && (
        <Callout
          icon={<WarningSign />}
          className="programming-problem-submission-form__warning"
          data-key="submission-warning"
        >
          {submissionWarning}
        </Callout>
      )
    );
  };

  const keys = Object.keys(sourceKeys);

  const isSingleSourceCode = keys.length === 1 && sourceKeys[keys[0]] === 'Source code';

  const renderSourceEditor = gradingLanguage => {
    const key = keys[0];
    const fieldText = { name: 'sourceTexts.' + key };
    return <Field component={FormAceEditor} gradingLanguage={gradingLanguage} {...fieldText} />;
  };

  const renderSourceFields = () => {
    let maxFileSize;
    if (isOutputOnly(gradingEngine)) {
      maxFileSize = MaxFileSize10MB;
    } else {
      maxFileSize = MaxFileSize300KB;
    }

    return keys.sort().map(key => {
      const fieldFile = {
        name: 'sourceFiles.' + key,
        label: isSingleSourceCode ? '... or submit source code file' : sourceKeys[key],
        validate: composeValidators(maxFileSize, CompatibleFilenameExtensionForGradingLanguage),
      };
      return <Field key={key} component={FormTableFileInput} {...fieldFile} />;
    });
  };

  const renderGradingLanguageFields = () => {
    if (isOutputOnly(gradingEngine)) {
      return null;
    }

    const field = {
      name: 'gradingLanguage',
      label: 'Language',
      validate: Required,
      optionValues: gradingLanguages,
      optionNamesMap: gradingLanguageNamesMap,
    };

    return <Field component={FormTableSelect2} {...field} />;
  };

  const isSubmitButtonDisabled = values => {
    return !values.sourceTexts && Object.keys(values.sourceFiles ?? {}).length !== keys.length;
  };

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ values, handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          {renderWarning()}
          {isSingleSourceCode && renderSourceEditor(values.gradingLanguage)}
          <table className="programming-problem-submission-form__table">
            <tbody>
              {renderSourceFields()}
              {renderGradingLanguageFields()}
            </tbody>
          </table>
          <Button
            type="submit"
            text="Submit"
            intent={Intent.PRIMARY}
            loading={submitting}
            disabled={isSubmitButtonDisabled(values)}
          />
        </form>
      )}
    </Form>
  );
}

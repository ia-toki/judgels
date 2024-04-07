import { Button, Callout, Intent } from '@blueprintjs/core';
import { WarningSign } from '@blueprintjs/icons';
import { Field, Form } from 'react-final-form';

import { isOutputOnly } from '../../../../modules/api/gabriel/engine';
import { gradingLanguageNamesMap } from '../../../../modules/api/gabriel/language.js';
import { FormTableFileInput } from '../../../forms/FormTableFileInput/FormTableFileInput';
import { FormTableSelect2 } from '../../../forms/FormTableSelect2/FormTableSelect2';
import { FormTableTextArea } from '../../../forms/FormTableTextArea/FormTableTextArea.jsx';
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
        const fieldFile = {
          name: 'sourceFiles.' + key,
          label: sourceKeys[key] + ' file',
          validate: composeValidators( maxFileSize, CompatibleFilenameExtensionForGradingLanguage),
        };
        const fieldText = {
          name: 'sourceTexts.' + key,
          label: sourceKeys[key],
          isCode: true,
          rows: 10,
        };
        return (
          <>
            <Field
              component={FormTableTextArea}
              keyClassName="programming-problem-submission-form__table_key"
              {...fieldText}
            />
            <Field key={key} component={FormTableFileInput} {...fieldFile} />
          </>
        );
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

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
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
      )}
    </Form>
  );
}

import { Button, Callout, Intent, Tag } from '@blueprintjs/core';
import { BanCircle } from '@blueprintjs/icons';
import { Field, Form } from 'react-final-form';

import { ContentCard } from '../../../ContentCard/ContentCard';
import { MaxCodeLength50KB, Required, composeValidators } from '../../../forms/validations';
import FormAceEditor from '../../../forms/FormAceEditor/FormAceEditor';
import { FormSelect2 } from '../../../forms/FormSelect2/FormSelect2';
import {
  getAllowedGradingLanguages,
  getGradingLanguageEditorSubmissionFilename,
  getGradingLanguageEditorSubmissionHint,
  gradingLanguageNamesMap,
} from '../../../../modules/api/gabriel/language.js';
import { decodeBase64 } from '../../../../utils/base64';

import './ProblemSubmissionEditor.scss';

export function ProblemSubmissionEditor({
  skeletons,
  config: { sourceKeys, gradingEngine, gradingLanguageRestriction },
  onSubmit,
  reasonNotAllowedToSubmit,
  preferredGradingLanguage,
}) {
  const onSubmitEditor = data => {
    const sourceFiles = {};
    Object.keys(sourceKeys).forEach(key => {
      sourceFiles[key] = new File([data.editor], getGradingLanguageEditorSubmissionFilename(data.gradingLanguage), {
        type: 'text/plain',
      });
    });

    return onSubmit({
      gradingLanguage: data.gradingLanguage,
      sourceFiles,
    });
  };

  const renderEditor = () => {
    if (reasonNotAllowedToSubmit) {
      return (
        <Callout icon={<BanCircle />} className="secondary-info">
          <span data-key="reason-not-allowed-to-submit">{this.props.reasonNotAllowedToSubmit}</span>
        </Callout>
      );
    }

    const gradingLanguages = getAllowedGradingLanguages(gradingEngine, gradingLanguageRestriction);

    let defaultGradingLanguage = preferredGradingLanguage;
    if (gradingLanguages.indexOf(defaultGradingLanguage) === -1) {
      defaultGradingLanguage = gradingLanguages.length === 0 ? undefined : gradingLanguages[0];
    }

    const gradingLanguageField = {
      name: 'gradingLanguage',
      validate: Required,
      optionValues: gradingLanguages,
      optionNamesMap: gradingLanguageNamesMap,
      small: true,
    };

    const editorField = {
      name: 'editor',
      validate: composeValidators(Required, MaxCodeLength50KB),
      autoFocus: true,
    };

    const initialValues = {
      gradingLanguage: defaultGradingLanguage,
    };

    (skeletons || []).forEach(skeleton => {
      if (skeleton.languages.indexOf(defaultGradingLanguage) >= 0) {
        initialValues.editor = decodeBase64(skeleton.content);
      }
    });

    return (
      <Form onSubmit={onSubmitEditor} initialValues={initialValues}>
        {({ values, handleSubmit, submitting, dirty }) => {
          const submissionHint = getGradingLanguageEditorSubmissionHint(values.gradingLanguage);

          return (
            <form onSubmit={handleSubmit}>
              <div className="editor-heading">
                <Field component={FormSelect2} {...gradingLanguageField} />
                <p>
                  <Tag intent={Intent.WARNING}>BETA</Tag>
                </p>
                <p>
                  <small>Type or paste your code here</small>
                </p>
              </div>
              {submissionHint && (
                <p>
                  <small>{submissionHint}</small>
                </p>
              )}
              <Field component={FormAceEditor} {...editorField} gradingLanguage={values.gradingLanguage} />
              <Button type="submit" text="Submit" intent={Intent.PRIMARY} loading={submitting} disabled={!dirty} />
            </form>
          );
        }}
      </Form>
    );
  };

  return <ContentCard>{renderEditor()}</ContentCard>;
}

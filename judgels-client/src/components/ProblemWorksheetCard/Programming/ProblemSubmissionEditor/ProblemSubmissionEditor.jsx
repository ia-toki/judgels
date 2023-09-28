import { Button, Callout, Intent, Tag } from '@blueprintjs/core';
import { BanCircle } from '@blueprintjs/icons';
import { Field, Form } from 'react-final-form';

import { ContentCard } from '../../../ContentCard/ContentCard';
import { MaxCodeLength50KB, Required } from '../../../forms/validations';
import FormAceEditor from '../../../forms/FormAceEditor/FormAceEditor';
import { FormSelect2 } from '../../../forms/FormSelect2/FormSelect2';
import { getAllowedGradingLanguages, gradingLanguageNamesMap } from '../../../../modules/api/gabriel/language.js';

import './ProblemSubmissionEditor.scss';

export function ProblemSubmissionEditor({
  config: { sourceKeys, gradingEngine, gradingLanguageRestriction },
  onSubmit,
  reasonNotAllowedToSubmit,
  preferredGradingLanguage,
}) {
  const onSubmitEditor = data => {
    const sourceFiles = {};
    Object.keys(sourceKeys).forEach(key => {
      sourceFiles[key] = new File([data.editor], 'solution.cpp', { type: 'text/plain' });
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
      validate: MaxCodeLength50KB,
      autoFocus: true,
    };

    const initialValues = {
      gradingLanguage: defaultGradingLanguage,
    };

    return (
      <Form onSubmit={onSubmitEditor} initialValues={initialValues}>
        {({ values, handleSubmit, submitting }) => (
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
            <Field component={FormAceEditor} {...editorField} gradingLanguage={values.gradingLanguage} />
            <Button type="submit" text="Submit" intent={Intent.PRIMARY} loading={submitting} />
          </form>
        )}
      </Form>
    );
  };

  return <ContentCard>{renderEditor()}</ContentCard>;
}

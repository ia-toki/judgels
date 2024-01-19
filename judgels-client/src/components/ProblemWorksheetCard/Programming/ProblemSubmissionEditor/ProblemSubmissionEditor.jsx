import { Button, Callout, Intent, Tag } from '@blueprintjs/core';
import { BanCircle } from '@blueprintjs/icons';
import classNames from 'classnames';
import { Component } from 'react';
import { Field, Form } from 'react-final-form';

import {
  getAllowedGradingLanguages,
  getGradingLanguageEditorSubmissionFilename,
  getGradingLanguageEditorSubmissionHint,
  gradingLanguageNamesMap,
} from '../../../../modules/api/gabriel/language.js';
import { decodeBase64 } from '../../../../utils/base64';
import { ContentCard } from '../../../ContentCard/ContentCard';
import FormAceEditor from '../../../forms/FormAceEditor/FormAceEditor';
import { FormSelect2 } from '../../../forms/FormSelect2/FormSelect2';
import { MaxCodeLength50KB, Required, composeValidators } from '../../../forms/validations';

import './ProblemSubmissionEditor.scss';

export class ProblemSubmissionEditor extends Component {
  state = {
    isResponsiveButtonClicked: false,
  };

  onSubmitEditor = data => {
    const {
      config: { sourceKeys },
      onSubmit,
    } = this.props;

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

  renderEditor = () => {
    const {
      skeletons,
      lastSubmissionSource,
      config: { gradingEngine, gradingLanguageRestriction },
      reasonNotAllowedToSubmit,
      preferredGradingLanguage,
    } = this.props;

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

    if (lastSubmissionSource) {
      Object.keys(lastSubmissionSource.submissionFiles).forEach(key => {
        initialValues.editor = decodeBase64(lastSubmissionSource.submissionFiles[key].content);
      });
    }

    return (
      <Form onSubmit={this.onSubmitEditor} initialValues={initialValues}>
        {({ values, handleSubmit, submitting, dirty }) => {
          const submissionHint = getGradingLanguageEditorSubmissionHint(values.gradingLanguage);

          return (
            <form onSubmit={handleSubmit} className={classNames({ show: this.state.isResponsiveButtonClicked })}>
              <div className="editor-header">
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

  clickResponsiveButton = () => {
    this.setState({ isResponsiveButtonClicked: true });
  };

  renderResponsiveButton = () => {
    if (this.state.isResponsiveButtonClicked) {
      return null;
    }
    return <Button className="responsive-button" text="Click to solve" small onClick={this.clickResponsiveButton} />;
  };

  render() {
    return (
      <ContentCard className="problem-submission-editor">
        {this.renderResponsiveButton()}
        {this.renderEditor()}
      </ContentCard>
    );
  }
}

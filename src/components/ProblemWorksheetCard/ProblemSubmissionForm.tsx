import { Button, Callout, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { change, Field, InjectedFormProps, reduxForm } from 'redux-form';

import { FormTableFileInput } from '../forms/FormTableFileInput/FormTableFileInput';
import { FormTableSelect2 } from '../forms/FormTableSelect2/FormTableSelect2';
import { Required } from '../forms/validations';
import { ProblemSubmissionConfig } from '../../modules/api/sandalphon/problem';
import { GradingEngineCode } from '../../modules/api/gabriel/engine';
import {
  gradingLanguageNamesMap,
  getAllowedGradingLanguages,
  preferredGradingLanguage,
} from '../../modules/api/gabriel/language';

import './ProblemSubmissionForm.css';
import { connect } from 'react-redux';

export interface ProblemSubmissionFormData {
  gradingLanguage: string;
  sourceFiles: { [key: string]: File };
}

interface RawProblemSubmissionFormProps extends InjectedFormProps<ProblemSubmissionFormData> {
  sourceKeys: { [key: string]: string };
  gradingEngine: string;
  gradingLanguages: string[];
  submissionWarning?: string;
}

class RawProblemSubmissionForm extends React.PureComponent<RawProblemSubmissionFormProps> {
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
        <Callout icon="warning-sign" className="problem-submission-form__warning">
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
          validate: [Required],
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

const RawConnectedProblemSubmissionForm = reduxForm<ProblemSubmissionFormData>({ form: 'problem-submission' })(
  RawProblemSubmissionForm
);

interface ProblemSubmissionFormProps {
  config: ProblemSubmissionConfig;
  onSubmit: (data: ProblemSubmissionFormData) => Promise<void>;
  onSetDefaultGradingLanguage: (gradingLanguage: string | null) => void;
  submissionWarning?: string;
}

interface ProblemSubmissionFormState {
  gradingLanguages?: string[];
}

class ProblemSubmissionForm extends React.PureComponent<ProblemSubmissionFormProps, ProblemSubmissionFormState> {
  state: ProblemSubmissionFormState = {};

  componentDidMount() {
    const gradingLanguages = getAllowedGradingLanguages(this.props.config.gradingLanguageRestriction);
    this.setState({ gradingLanguages });

    let defaultGradingLanguage: string | null = preferredGradingLanguage;
    if (gradingLanguages.indexOf(defaultGradingLanguage) === -1) {
      defaultGradingLanguage = gradingLanguages.length === 0 ? null : gradingLanguages[0];
    }
    this.props.onSetDefaultGradingLanguage(defaultGradingLanguage);
  }

  render() {
    if (!this.state.gradingLanguages) {
      return null;
    }

    const props = {
      onSubmit: this.props.onSubmit,
      sourceKeys: this.props.config.sourceKeys,
      gradingEngine: this.props.config.gradingEngine,
      gradingLanguages: this.state.gradingLanguages,
      submissionWarning: this.props.submissionWarning,
    };
    return <RawConnectedProblemSubmissionForm {...props} />;
  }
}

function createProblemSubmissionForm() {
  const mapDispatchToProps = dispatch => ({
    onSetDefaultGradingLanguage: gradingLanguage =>
      dispatch(change('problem-submission', 'gradingLanguage', gradingLanguage)),
  });

  return connect(undefined, mapDispatchToProps)(ProblemSubmissionForm);
}

export default createProblemSubmissionForm();

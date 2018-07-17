import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from '../../../../../../../../../../components/forms/validations';
import { FormTextInput } from '../../../../../../../../../../components/forms/FormTextInput/FormTextInput';
import { FormSelect2 } from '../../../../../../../../../../components/forms/FormSelect2/FormSelect2';
import { FormTextArea } from '../../../../../../../../../../components/forms/FormTextArea/FormTextArea';
import { constructProblemName } from '../../../../../../../../../../modules/api/sandalphon/problem';

export interface ContestClarificationCreateFormData {
  topicJid: string;
  title: string;
  question: string;
}

export interface ContestClarificationCreateFormProps extends InjectedFormProps<ContestClarificationCreateFormData> {
  contestJid: string;
  problemJids: string[];
  problemAliasesMap: { [problemJid: string]: string };
  problemNamesMap: { [problemJid: string]: string };

  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const ContestClarificationCreateForm = (props: ContestClarificationCreateFormProps) => {
  const topicField: any = {
    name: 'topicJid',
    label: 'Topic',
    validate: [Required],
    optionValues: [props.contestJid, ...props.problemJids],
    optionNamesMap: {
      [props.contestJid]: '(General)',
      ...Object.assign(
        {},
        ...props.problemJids.map(jid => ({
          [jid]: constructProblemName(props.problemNamesMap[jid], props.problemAliasesMap[jid]),
        }))
      ),
    },
  };

  const titleField: any = {
    name: 'title',
    label: 'Title',
    validate: [Required],
  };

  const questionField: any = {
    name: 'question',
    label: 'Question',
    validate: [Required],
  };

  const fields = (
    <>
      <Field component={FormSelect2} {...topicField} />
      <Field component={FormTextInput} {...titleField} />
      <Field component={FormTextArea} {...questionField} />
    </>
  );

  const submitButton = <Button type="submit" text="Submit" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<ContestClarificationCreateFormData>({ form: 'contest-clarification-create' })(
  ContestClarificationCreateForm
);

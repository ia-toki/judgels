import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from 'components/forms/validations';
import { FormTextInput } from 'components/forms/FormTextInput/FormTextInput';
import { FormSelect2 } from 'components/forms/FormSelect2/FormSelect2';
import { FormRichTextArea } from 'components/forms/FormRichTextArea/FormRichTextArea';
import { ContestAnnouncementStatus } from 'modules/api/uriel/contestAnnouncement';

export interface ContestAnnouncementCreateFormData {
  status: string;
  title: string;
  content: string;
}

export interface ContestAnnouncementCreateFormProps extends InjectedFormProps<ContestAnnouncementCreateFormData> {
  renderFormComponents: (fields: JSX.Element, submitButton: JSX.Element) => JSX.Element;
}

const ContestAnnouncementCreateForm = (props: ContestAnnouncementCreateFormProps) => {
  const statusField: any = {
    name: 'status',
    label: 'Status',
    validate: [Required],
    optionValues: [ContestAnnouncementStatus.Draft, ContestAnnouncementStatus.Published],
    optionNamesMap: { [ContestAnnouncementStatus.Draft]: 'Draft', [ContestAnnouncementStatus.Published]: 'Published' },
  };

  const titleField: any = {
    name: 'title',
    label: 'Title',
    validate: [Required],
  };

  const contentField: any = {
    name: 'content',
    label: 'Content',
    validate: [Required],
  };

  const fields = (
    <>
      <Field component={FormSelect2} {...statusField} />
      <Field component={FormTextInput} {...titleField} />
      <Field component={FormRichTextArea} {...contentField} />
    </>
  );

  const submitButton = <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={props.submitting} />;

  return <form onSubmit={props.handleSubmit}>{props.renderFormComponents(fields, submitButton)}</form>;
};

export default reduxForm<ContestAnnouncementCreateFormData>({
  form: 'contest-announcement-create',
  initialValues: { status: ContestAnnouncementStatus.Published },
})(ContestAnnouncementCreateForm);

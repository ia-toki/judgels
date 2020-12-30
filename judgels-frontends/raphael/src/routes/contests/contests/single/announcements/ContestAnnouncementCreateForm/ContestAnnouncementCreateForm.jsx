import { Button, Intent } from '@blueprintjs/core';
import { Field, reduxForm } from 'redux-form';

import { Required } from '../../../../../../components/forms/validations';
import { FormTextInput } from '../../../../../../components/forms/FormTextInput/FormTextInput';
import { FormSelect2 } from '../../../../../../components/forms/FormSelect2/FormSelect2';
import { FormRichTextArea } from '../../../../../../components/forms/FormRichTextArea/FormRichTextArea';
import { ContestAnnouncementStatus } from '../../../../../../modules/api/uriel/contestAnnouncement';

function ContestAnnouncementCreateForm({ handleSubmit, submitting, renderFormComponents }) {
  const statusField = {
    name: 'status',
    label: 'Status',
    validate: [Required],
    optionValues: [ContestAnnouncementStatus.Draft, ContestAnnouncementStatus.Published],
    optionNamesMap: { [ContestAnnouncementStatus.Draft]: 'Draft', [ContestAnnouncementStatus.Published]: 'Published' },
  };

  const titleField = {
    name: 'title',
    label: 'Title',
    validate: [Required],
    autoFocus: true,
  };

  const contentField = {
    name: 'content',
    label: 'Content',
    rows: 15,
    validate: [Required],
  };

  const fields = (
    <>
      <Field component={FormSelect2} {...statusField} />
      <Field component={FormTextInput} {...titleField} />
      <Field component={FormRichTextArea} {...contentField} />
    </>
  );

  const submitButton = <Button type="submit" text="Create" intent={Intent.PRIMARY} loading={submitting} />;

  return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
}

export default reduxForm({
  form: 'contest-announcement-create',
  initialValues: { status: ContestAnnouncementStatus.Published },
  touchOnBlur: false,
})(ContestAnnouncementCreateForm);

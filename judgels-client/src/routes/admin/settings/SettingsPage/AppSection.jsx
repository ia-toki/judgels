import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { useEditableSection } from '../../../../components/EditableSections/EditableSections';
import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { FormRichTextArea } from '../../../../components/forms/FormRichTextArea/FormRichTextArea';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { FormTableTextInput } from '../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { updateSettingsMutationOptions } from '../../../../modules/queries/setting';

import * as toastActions from '../../../../modules/toast/toastActions';

export function AppSection({ app }) {
  const updateSettingsMutation = useMutation(updateSettingsMutationOptions());

  const { isEditing, isEditDisabled, setIsEditing } = useEditableSection();

  const keyStyles = { width: '300px' };

  const rows = [
    { key: 'name', title: 'Name', value: app.name },
    { key: 'slogan', title: 'Slogan', value: app.slogan },
    { key: 'announcement', title: 'Announcement', value: <HtmlText>{app.announcement || ''}</HtmlText> },
  ];

  const updateSettings = async values => {
    await updateSettingsMutation.mutateAsync(
      { app: { name: values.name, slogan: values.slogan, announcement: values.announcement || '' } },
      {
        onSuccess: () => toastActions.showSuccessToast('Settings updated.'),
      }
    );
    setIsEditing(false);
  };

  const renderEditButton = () => {
    return (
      !isEditing && (
        <Button small variant="outlined" icon={<Edit />} disabled={isEditDisabled} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = {
        name: app.name,
        slogan: app.slogan,
        announcement: app.announcement,
      };
      return (
        <Form onSubmit={updateSettings} initialValues={initialValues}>
          {({ handleSubmit, submitting }) => (
            <Flex asChild flexDirection="column" gap={2}>
              <form onSubmit={handleSubmit}>
                <HTMLTable striped>
                  <tbody>
                    <Field component={FormTableTextInput} keyStyles={keyStyles} name="name" label="Name" />
                    <Field component={FormTableTextInput} keyStyles={keyStyles} name="slogan" label="Slogan" />
                  </tbody>
                </HTMLTable>
                <Field component={FormRichTextArea} rows={5} name="announcement" label="Announcement" />
                <ActionButtons justifyContent="end">
                  <Button text="Cancel" disabled={submitting} onClick={() => setIsEditing(false)} />
                  <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
                </ActionButtons>
              </form>
            </Flex>
          )}
        </Form>
      );
    }

    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="center" gap={2}>
        <h4>
          <span>App</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}

import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { callAction } from '../../../../../../modules/callAction';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import ContestEditDescriptionForm from '../ContestEditDescriptionForm/ContestEditDescriptionForm';

import * as contestActions from '../../../modules/contestActions';

export default function ContestEditDescriptionTab() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const [state, setState] = useState({
    isEditing: false,
    response: undefined,
  });

  const refreshContestDescription = async () => {
    const response = await callAction(contestActions.getContestDescription(contest.jid));
    setState(prevState => ({ ...prevState, response }));
  };

  useEffect(() => {
    refreshContestDescription();
  }, [contest.jid]);

  const render = () => {
    return (
      <>
        <h4>
          Description settings
          {renderEditButton()}
        </h4>
        <hr />
        {renderContent()}
      </>
    );
  };

  const renderEditButton = () => {
    return (
      !state.isEditing && (
        <Button small className="right-action-button" intent={Intent.PRIMARY} icon={<Edit />} onClick={toggleEdit}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    const { isEditing, response } = state;
    if (response === undefined) {
      return <LoadingState />;
    }
    if (isEditing) {
      const initialValues = {
        description: response.description,
      };
      const formProps = {
        onCancel: toggleEdit,
      };
      return (
        <ContestEditDescriptionForm initialValues={initialValues} onSubmit={updateContestDescription} {...formProps} />
      );
    }
    return renderDescription(response);
  };

  const renderDescription = ({ description, profilesMap }) => {
    if (!description) {
      return (
        <p>
          <small>No description.</small>
        </p>
      );
    }
    return (
      <ContentCard className="contest-edit-dialog__content">
        <HtmlText profilesMap={profilesMap}>{description}</HtmlText>
      </ContentCard>
    );
  };

  const updateContestDescription = async data => {
    await callAction(contestActions.updateContestDescription(contest.jid, data.description));
    await refreshContestDescription();
    toggleEdit();
  };

  const toggleEdit = () => {
    setState(prevState => ({ ...prevState, isEditing: !prevState.isEditing }));
  };

  return render();
}

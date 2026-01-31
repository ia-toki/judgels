import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import ContestEditDescriptionForm from '../ContestEditDescriptionForm/ContestEditDescriptionForm';

import * as contestActions from '../../../modules/contestActions';

export default function ContestEditDescriptionTab() {
  const { contestSlug } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const dispatch = useDispatch();

  const [state, setState] = useState({
    isEditing: false,
    response: undefined,
  });

  const refreshContestDescription = async () => {
    const response = await dispatch(contestActions.getContestDescription(contest.jid));
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
    await dispatch(contestActions.updateContestDescription(contest.jid, data.description));
    await refreshContestDescription();
    toggleEdit();
  };

  const toggleEdit = () => {
    setState(prevState => ({ ...prevState, isEditing: !prevState.isEditing }));
  };

  return render();
}

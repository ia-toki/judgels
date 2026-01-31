import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useQueryClient, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { formatDuration, parseDuration } from '../../../../../../utils/duration';
import ContestEditGeneralForm from '../ContestEditGeneralForm/ContestEditGeneralForm';
import { ContestEditGeneralTable } from '../ContestEditGeneralTable/ContestEditGeneralTable';

import * as contestActions from '../../../modules/contestActions';

export default function ContestEditGeneralTab() {
  const { contestSlug } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const dispatch = useDispatch();
  const queryClient = useQueryClient();

  const [state, setState] = useState({
    isEditing: false,
  });

  const render = () => {
    return (
      <>
        <h4>
          General settings
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
    if (state.isEditing) {
      const initialValues = {
        slug: contest.slug,
        name: contest.name,
        style: contest.style,
        beginTime: new Date(contest.beginTime).toISOString(),
        duration: formatDuration(contest.duration),
      };
      const formProps = {
        onCancel: toggleEdit,
      };
      return <ContestEditGeneralForm initialValues={initialValues} onSubmit={updateContest} {...formProps} />;
    }
    return <ContestEditGeneralTable contest={contest} />;
  };

  const updateContest = async data => {
    const updateData = {
      slug: data.slug,
      name: data.name,
      style: data.style,
      beginTime: new Date(data.beginTime).getTime(),
      duration: parseDuration(data.duration),
    };
    await dispatch(contestActions.updateContest(contest.jid, contest.slug, updateData));
    await queryClient.invalidateQueries({ queryKey: ['contest-by-slug', contestSlug] });
    toggleEdit();
  };

  const toggleEdit = () => {
    setState(prevState => ({ ...prevState, isEditing: !prevState.isEditing }));
  };

  return render();
}

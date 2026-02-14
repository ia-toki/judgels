import { Button, Card, Classes, Dialog, Intent } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import EditorialLanguageWidget from '../../../../../../../components/LanguageWidget/EditorialLanguageWidget';
import { ProblemEditorial } from '../../../../../../../components/ProblemEditorial/ProblemEditorial';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../../modules/queries/problemSet';
import { selectToken } from '../../../../../../../modules/session/sessionSelectors';
import { selectEditorialLanguage } from '../../../../../../../modules/webPrefs/webPrefsSelectors';

import * as problemSetProblemActions from '../../modules/problemSetProblemActions';

import './ProblemEditorialDialog.scss';

export default function ProblemEditorialDialog({ settersMap, profilesMap }) {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const dispatch = useDispatch();
  const token = useSelector(selectToken);
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(token, problemSet.jid, problemAlias));
  const editorialLanguage = useSelector(selectEditorialLanguage);

  const [state, setState] = useState({
    isDialogOpen: false,
    response: undefined,
  });

  const loadEditorial = async () => {
    setState(prevState => ({ ...prevState, response: undefined }));

    const response = await dispatch(
      problemSetProblemActions.getProblemEditorial(problemSet.jid, problemAlias, editorialLanguage)
    );

    setState(prevState => ({ ...prevState, response }));
  };

  useEffect(() => {
    if (state.isDialogOpen) {
      loadEditorial();
    }
  }, [editorialLanguage, state.isDialogOpen]);

  const render = () => {
    return (
      <div>
        {renderButton()}
        {renderDialog()}
      </div>
    );
  };

  const renderButton = () => {
    return (
      <Button
        className="problem-editorial-dialog-button"
        intent={Intent.WARNING}
        small
        onClick={toggleDialog}
        disabled={state.isDialogOpen}
      >
        View editorial
      </Button>
    );
  };

  const renderDialog = () => {
    const { isDialogOpen } = state;
    return (
      <Dialog className="problem-editorial-dialog" isOpen={isDialogOpen} onClose={toggleDialog} title="Editorial">
        <div className={Classes.DIALOG_BODY}>
          {renderEditorialLanguageWidget()}
          <Card className="problem-editorial-card">{renderEditorial()}</Card>
        </div>
      </Dialog>
    );
  };

  const renderEditorial = () => {
    const { response } = state;
    if (!response) {
      return null;
    }
    const { editorial } = response;

    return (
      <ProblemEditorial
        title={`${problemSet.name} - Problem ${problem.alias}`}
        containerName={problemSet.name}
        settersMap={settersMap}
        profilesMap={profilesMap}
      >
        {editorial.text}
      </ProblemEditorial>
    );
  };

  const toggleDialog = () => {
    setState(prevState => ({ ...prevState, isDialogOpen: !prevState.isDialogOpen }));
  };

  const renderEditorialLanguageWidget = () => {
    const { response } = state;
    if (!response) {
      return null;
    }
    const { defaultLanguage, languages } = response.editorial;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const props = {
      defaultLanguage,
      editorialLanguages: languages,
    };
    return (
      <div className="language-widget-wrapper">
        <EditorialLanguageWidget {...props} />
      </div>
    );
  };

  return render();
}

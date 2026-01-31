import { Button, Classes, Dialog, HTMLTable } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import classNames from 'classnames';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { getCountryName } from '../../../../../../assets/data/countries';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';

import * as contestContestantActions from '../../modules/contestContestantActions';

import './ContestRegistrantsDialog.scss';

export default function ContestRegistrantsDialog({ onClose }) {
  const { contestSlug } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const dispatch = useDispatch();

  const [state, setState] = useState({
    response: undefined,
  });

  const refreshRegistrants = async () => {
    const response = await dispatch(contestContestantActions.getApprovedContestants(contest.jid));
    setState(prevState => ({ ...prevState, response }));
  };

  useEffect(() => {
    refreshRegistrants();
  }, []);

  const render = () => {
    const { response } = state;
    const contestantsCount = response ? ` (${response.data.length})` : '';

    return (
      <Dialog isOpen onClose={onClose} title={`Registrants${contestantsCount}`} canOutsideClickClose={false}>
        <div className={classNames(Classes.DIALOG_BODY, 'contest-registrants-dialog__body')}>{renderRegistrants()}</div>
        <div className={Classes.DIALOG_FOOTER}>
          <div className={Classes.DIALOG_FOOTER_ACTIONS}>
            <Button text="Close" onClick={onClose} />
          </div>
        </div>
      </Dialog>
    );
  };

  const renderRegistrants = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: contestants, profilesMap } = response;
    const sortedContestants = contestants.slice().sort((jid1, jid2) => {
      const rating1 = (profilesMap[jid1] && profilesMap[jid1].rating && profilesMap[jid1].rating.publicRating) || 0;
      const rating2 = (profilesMap[jid2] && profilesMap[jid2].rating && profilesMap[jid2].rating.publicRating) || 0;
      if (rating1 !== rating2) {
        return rating2 - rating1;
      }

      const country1 = (profilesMap[jid1] && getCountryName(profilesMap[jid1].country)) || 'ZZ';
      const country2 = (profilesMap[jid2] && getCountryName(profilesMap[jid2].country)) || 'ZZ';
      if (country1 !== country2) {
        return country1.localeCompare(country2);
      }

      const username1 = (profilesMap[jid1] && profilesMap[jid1].username) || 'ZZ';
      const username2 = (profilesMap[jid2] && profilesMap[jid2].username) || 'ZZ';
      return username1.localeCompare(username2);
    });

    const rows = sortedContestants.map(jid => (
      <tr key={jid}>
        <td>{profilesMap[jid] && getCountryName(profilesMap[jid].country)}</td>
        <td>
          <UserRef profile={profilesMap[jid]} showFlag />
        </td>
      </tr>
    ));

    return (
      <HTMLTable striped className="table-list">
        <thead>
          <tr>
            <th>Country</th>
            <th>User</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return render();
}

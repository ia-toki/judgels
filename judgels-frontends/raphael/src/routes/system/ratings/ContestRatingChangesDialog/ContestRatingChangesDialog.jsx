import { Button, Classes, Dialog, HTMLTable, Intent } from '@blueprintjs/core';
import classNames from 'classnames';

import { UserRef } from '../../../../components/UserRef/UserRef';
import { getRatingClass } from '../../../../modules/api/jophiel/userRating';

export function ContestRatingChangesDialog({ contest, ratingChanges, onClose, onApply, isApplying }) {
  const renderTable = () => {
    const { ratingsMap, profilesMap } = ratingChanges;
    const userJids = Object.keys(ratingsMap);
    userJids.sort((jid1, jid2) => {
      const rating1 = ratingsMap[jid1].publicRating;
      const rating2 = ratingsMap[jid2].publicRating;
      return rating2 - rating1;
    });

    return (
      <HTMLTable striped className="table-list">
        <thead>
          <tr>
            <th>Username</th>
            <th>New rating</th>
          </tr>
        </thead>
        <tbody>
          {userJids.map(userJid => (
            <tr key={userJid}>
              <td>
                <UserRef profile={profilesMap[userJid]} />
              </td>
              <td>
                <span className={getRatingClass(ratingsMap[userJid])}>{ratingsMap[userJid].publicRating}</span>
              </td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    );
  };

  return (
    <Dialog
      className="contest-rating-changes-dialog"
      isOpen
      onClose={onClose}
      title={contest.name}
      canOutsideClickClose={false}
    >
      <div className={classNames(Classes.DIALOG_BODY, 'contest-rating-changes-dialog__body')}>{renderTable()}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button
            intent={Intent.PRIMARY}
            text="Apply rating changes"
            onClick={onApply}
            disabled={isApplying}
            loading={isApplying}
          />
        </div>
      </div>
    </Dialog>
  );
}

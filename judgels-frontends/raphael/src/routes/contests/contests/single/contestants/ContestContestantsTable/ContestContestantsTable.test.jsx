import { shallow, ShallowWrapper } from 'enzyme';
import * as React from 'react';

import { ContestContestantsTable } from './ContestContestantsTable';
import { UserRef } from '../../../../../../components/UserRef/UserRef';
import { FormattedDate } from '../../../../../../components/FormattedDate/FormattedDate';
import { ProgressBar } from '../../../../../../components/ProgressBar/ProgressBar';
import { VirtualModuleConfig } from '../../../../../../modules/api/uriel/contestModule';
import { Contest } from '../../../../../../modules/api/uriel/contest';
import { ContestContestant } from '../../../../../../modules/api/uriel/contestContestant';

describe('ContestContestantsTable', () => {
  let virtualModuleConfig: VirtualModuleConfig;
  let contestants: ContestContestant[];
  let now: number;

  let wrapper: ShallowWrapper;

  const render = () => {
    const props = {
      contest: {
        beginTime: 10,
        duration: 100,
      } as Contest,
      virtualModuleConfig,
      contestants,
      profilesMap: {
        userJid1: { username: 'userC' },
        userJid2: { username: 'userD' },
        userJid3: { username: 'userA' },
        userJid4: { username: 'userE' },
        userJid5: { username: 'userB' },
      },
      now,
    };

    wrapper = shallow(<ContestContestantsTable {...props} />);
  };

  const mapProgressBar = progressBar => {
    return progressBar && [progressBar.props().num, progressBar.props().denom];
  };

  const mapStartTime = formattedDate => {
    return formattedDate && formattedDate.props().value;
  };

  describe('when contest is not virtual', () => {
    beforeEach(() => {
      contestants = [
        { userJid: 'userJid1' },
        { userJid: 'userJid2' },
        { userJid: 'userJid3' },
        { userJid: 'userJid4' },
        { userJid: 'userJid5' },
      ];
      render();
    });

    it('shows the correct columns', () => {
      const usernames = wrapper
        .find('tbody')
        .children()
        .map(
          tr =>
            tr
              .childAt(1)
              .find(UserRef)
              .props().profile.username
        );
      expect(usernames).toEqual(['userA', 'userB', 'userC', 'userD', 'userE']);
    });
  });

  describe('when contest is virtual', () => {
    beforeEach(() => {
      now = 70;
      virtualModuleConfig = { virtualDuration: 50 };
      contestants = [
        { userJid: 'userJid1' },
        { userJid: 'userJid2', contestStartTime: 30 },
        { userJid: 'userJid3', contestStartTime: 20 },
        { userJid: 'userJid4', contestStartTime: 65 },
        { userJid: 'userJid5' },
      ];
      render();
    });

    it('shows the correct columns', () => {
      const usernames = wrapper
        .find('tbody')
        .children()
        .map(
          tr =>
            tr
              .childAt(1)
              .find(UserRef)
              .props().profile.username
        );
      expect(usernames).toEqual(['userA', 'userD', 'userE', 'userB', 'userC']);

      const progresses = wrapper
        .find('tbody')
        .children()
        .map(tr =>
          tr
            .childAt(2)
            .find(ProgressBar)
            .map(mapProgressBar)
        );
      expect(progresses).toEqual([[[50, 50]], [[40, 50]], [[10, 50]], [], []]);

      const startTimes = wrapper
        .find('tbody')
        .children()
        .map(tr =>
          tr
            .childAt(3)
            .find(FormattedDate)
            .map(mapStartTime)
        );
      expect(startTimes).toEqual([[20], [30], [65], [], []]);
    });
  });
});

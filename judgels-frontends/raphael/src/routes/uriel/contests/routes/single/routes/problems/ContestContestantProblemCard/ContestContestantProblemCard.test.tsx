import { shallow, ShallowWrapper } from 'enzyme';
import * as React from 'react';

import { ContentCardLink } from '../../../../../../../../components/ContentCardLink/ContentCardLink';
import { ContestContestantProblemCard, ContestContestantProblemCardProps } from './ContestContestantProblemCard';
import { Contest } from '../../../../../../../../modules/api/uriel/contest';
import {
  ContestContestantProblem,
  ContestProblem,
  ContestProblemStatus,
} from '../../../../../../../../modules/api/uriel/contestProblem';

describe('ContestContestantProblemCard', () => {
  let wrapper: ShallowWrapper<ContestContestantProblemCardProps>;

  const render = (problem: ContestProblem) => {
    const contest = { id: 123, jid: 'jid123' } as Contest;
    const contestantProblem: ContestContestantProblem = {
      problem,
      totalSubmissions: 10,
    };
    const problemName = 'The Problem';

    const props = { contest, contestantProblem, problemName };
    wrapper = shallow(<ContestContestantProblemCard {...props} />);
  };

  test('problem name', () => {
    render({
      problemJid: 'jid',
      alias: 'A',
      status: ContestProblemStatus.Open,
      submissionsLimit: 50,
    });

    expect(wrapper.find('[data-key="name"]').text()).toEqual('A. The Problem');
    expect(wrapper.find(ContentCardLink).props().to).toEqual('/contests/123/problems/A');
  });

  test('open problem with submissions limit', () => {
    render({
      problemJid: 'jid',
      alias: 'A',
      status: ContestProblemStatus.Open,
      submissionsLimit: 50,
    });

    expect(wrapper.find('[data-key="status"]').text()).toEqual('40 submissions left');
  });

  test('open problem without submissions limit', () => {
    render({
      problemJid: 'jid',
      alias: 'A',
      status: ContestProblemStatus.Open,
      submissionsLimit: 0,
    });

    expect(wrapper.find('[data-key="status"]').text()).toEqual('');
  });

  test('closed problem', () => {
    render({
      problemJid: 'jid',
      alias: 'A',
      status: ContestProblemStatus.Closed,
      submissionsLimit: 50,
    });

    expect(
      wrapper
        .find('[data-key="status"]')
        .childAt(0)
        .childAt(0)
        .text()
    ).toEqual('CLOSED');
  });
});

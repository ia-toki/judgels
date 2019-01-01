import { shallow, ShallowWrapper } from 'enzyme';
import * as React from 'react';

import { ContentCardLink } from 'components/ContentCardLink/ContentCardLink';
import { contest } from 'fixtures/state';
import { ContestProblem, ContestProblemStatus } from 'modules/api/uriel/contestProblem';

import { ContestProblemCard, ContestProblemCardProps } from './ContestProblemCard';

describe('ContestProblemCard', () => {
  let wrapper: ShallowWrapper<ContestProblemCardProps>;

  const render = (problem: ContestProblem) => {
    const totalSubmissions = 10;
    const problemName = 'The Problem';

    const props = { contest, problem, problemName, totalSubmissions };
    wrapper = shallow(<ContestProblemCard {...props} />);
  };

  test('problem name', () => {
    render({
      problemJid: 'jid',
      alias: 'A',
      status: ContestProblemStatus.Open,
      submissionsLimit: 50,
    });

    expect(wrapper.find('[data-key="name"]').text()).toEqual('A. The Problem');
    expect(wrapper.find(ContentCardLink).props().to).toEqual('/contests/contest-a/problems/A');
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

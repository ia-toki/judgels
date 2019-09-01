import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest, contestJid } from '../../../../../../fixtures/state';
import { ContestProblemStatus, ContestProblemData } from '../../../../../../modules/api/uriel/contestProblem';

import { ContestProblemEditDialog, ContestProblemEditDialogProps } from './ContestProblemEditDialog';

describe('ContestProblemEditDialog', () => {
  let onSetProblems: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  const problems: ContestProblemData[] = [
    {
      alias: 'A',
      slug: 'pp1',
      status: ContestProblemStatus.Open,
      submissionsLimit: undefined,
      points: 0,
    },
    {
      alias: 'B',
      slug: 'pp2',
      status: ContestProblemStatus.Open,
      submissionsLimit: 10,
      points: 0,
    },
    {
      alias: 'C',
      slug: 'pp3',
      status: ContestProblemStatus.Closed,
      submissionsLimit: undefined,
      points: 0,
    },
    {
      alias: 'D',
      slug: 'pp4',
      status: ContestProblemStatus.Closed,
      submissionsLimit: 10,
      points: 0,
    },
  ];

  beforeEach(() => {
    onSetProblems = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(combineReducers({ form: formReducer }));

    const props: ContestProblemEditDialogProps = {
      contest,
      problems,
      onSetProblems,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestProblemEditDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('edit problems dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const problemsField = wrapper.find('textarea[name="problems"]');
    expect(problemsField.prop('value')).toEqual('A,pp1\nB,pp2,OPEN,10\nC,pp3,CLOSED\nD,pp4,CLOSED,10');

    problemsField.simulate('change', { target: { value: 'P, qq1\n Q,qq2,OPEN,20\nR,qq3,CLOSED \nS,qq4,CLOSED,20' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onSetProblems).toHaveBeenCalledWith(contestJid, [
      {
        alias: 'P',
        slug: 'qq1',
        status: ContestProblemStatus.Open,
        submissionsLimit: undefined,
        points: undefined,
      },
      {
        alias: 'Q',
        slug: 'qq2',
        status: ContestProblemStatus.Open,
        submissionsLimit: 20,
        points: undefined,
      },
      {
        alias: 'R',
        slug: 'qq3',
        status: ContestProblemStatus.Closed,
        submissionsLimit: undefined,
        points: undefined,
      },
      {
        alias: 'S',
        slug: 'qq4',
        status: ContestProblemStatus.Closed,
        submissionsLimit: 20,
        points: undefined,
      },
    ]);
  });
});

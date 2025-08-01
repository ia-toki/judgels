import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import { parseDateTime } from '../../../../../../utils/datetime';
import { parseDuration } from '../../../../../../utils/duration';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestEditGeneralTab from './ContestEditGeneralTab';

import * as contestActions from '../../../modules/contestActions';
import * as contestWebActions from '../../modules/contestWebActions';

jest.mock('../../../modules/contestActions');
jest.mock('../../modules/contestWebActions');

describe('ContestEditGeneralTab', () => {
  let wrapper;

  beforeEach(() => {
    contestWebActions.getContestByJidWithWebConfig.mockReturnValue(() => Promise.resolve({}));
    contestActions.updateContest.mockReturnValue(() => Promise.resolve({}));

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(
      PutContest({
        jid: 'contestJid',
        slug: 'contest-a',
        name: 'Contest A',
        style: ContestStyle.ICPC,
        beginTime: parseDateTime('2018-09-10 13:00').getTime(),
      })
    );

    wrapper = mount(
      <Provider store={store}>
        <ContestEditGeneralTab />
      </Provider>
    );
  });

  test('form', async () => {
    const button = wrapper.find('button');
    button.simulate('click');

    const slug = wrapper.find('input[name="slug"]');
    expect(slug.prop('value')).toEqual('contest-a');
    slug.prop('onChange')({ target: { value: 'contest-b' } });

    const name = wrapper.find('input[name="name"]');
    expect(name.prop('value')).toEqual('Contest A');
    name.prop('onChange')({ target: { value: 'Contest B' } });

    const beginTime = wrapper.find('input[name="beginTime"]');
    beginTime.prop('onChange')({ target: { value: '2018-09-10 17:00' } });

    const duration = wrapper.find('input[name="duration"]');
    duration.prop('onChange')({ target: { value: '6h' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(contestActions.updateContest).toHaveBeenCalledWith('contestJid', 'contest-a', {
      slug: 'contest-b',
      name: 'Contest B',
      style: ContestStyle.ICPC,
      beginTime: parseDateTime('2018-09-10 17:00').getTime(),
      duration: parseDuration('6h'),
    });
  });
});

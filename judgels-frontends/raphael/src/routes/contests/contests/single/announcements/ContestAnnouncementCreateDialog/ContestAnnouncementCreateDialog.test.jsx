import { mount } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest, contestJid } from '../../../../../../fixtures/state';
import { ContestAnnouncementStatus } from '../../../../../../modules/api/uriel/contestAnnouncement';
import { ContestAnnouncementCreateDialog } from './ContestAnnouncementCreateDialog';

describe('ContestAnnouncementCreateDialog', () => {
  let onCreateAnnouncement;
  let wrapper;

  beforeEach(() => {
    onCreateAnnouncement = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
      contest,
      onCreateAnnouncement,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestAnnouncementCreateDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('create announcement dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const status = wrapper.find('button[data-key="status"]');
    // status.simulate('click');

    const title = wrapper.find('input[name="title"]');
    title.simulate('change', { target: { value: 'Snack' } });

    const content = wrapper.find('textarea[name="content"]');
    content.simulate('change', { target: { value: 'Snack is provided.' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateAnnouncement).toHaveBeenCalledWith(contestJid, {
      title: 'Snack',
      content: 'Snack is provided.',
      status: ContestAnnouncementStatus.Published,
    });
  });
});

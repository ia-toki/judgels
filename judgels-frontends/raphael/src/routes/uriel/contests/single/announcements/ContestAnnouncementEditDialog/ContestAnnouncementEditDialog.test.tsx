import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest, contestJid } from '../../../../../../fixtures/state';
import {
  ContestAnnouncement,
  ContestAnnouncementStatus,
} from '../../../../../../modules/api/uriel/contestAnnouncement';

import { ContestAnnouncementEditDialog, ContestAnnouncementEditDialogProps } from './ContestAnnouncementEditDialog';

describe('ContestAnnouncementEditDialog', () => {
  let onUpdateAnnouncement: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  const announcement = {
    jid: 'announcementJid123',
    title: 'Snack',
    content: 'Snack is provided.',
    status: ContestAnnouncementStatus.Published,
  } as ContestAnnouncement;

  beforeEach(() => {
    onUpdateAnnouncement = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const onToggleEditDialog = () => {
      return;
    };

    const store: any = createStore(combineReducers({ form: formReducer }));

    const props: ContestAnnouncementEditDialogProps = {
      contest,
      announcement,
      onToggleEditDialog,
      onUpdateAnnouncement,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestAnnouncementEditDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('edit announcement dialog form', () => {
    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const status = wrapper.find('button[data-key="status"]');
    // status.simulate('click');

    const title = wrapper.find('input[name="title"]');
    expect(title.prop('value')).toEqual('Snack');
    title.simulate('change', { target: { value: 'Snack [edited]' } });

    const content = wrapper.find('textarea[name="content"]');
    expect(content.prop('value')).toEqual('Snack is provided.');
    content.simulate('change', { target: { value: 'Snack is NOT provided.' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpdateAnnouncement).toHaveBeenCalledWith(contestJid, 'announcementJid123', {
      title: 'Snack [edited]',
      content: 'Snack is NOT provided.',
      status: ContestAnnouncementStatus.Published,
    });
  });
});

import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { CourseCreateDialog } from './CourseCreateDialog';

describe('CourseCreateDialog', () => {
  let onGetCourseConfig;
  let onCreateCourse;
  let wrapper;

  beforeEach(() => {
    onCreateCourse = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      onGetCourseConfig,
      onCreateCourse,
    };
    wrapper = mount(
      <Provider store={store}>
        <CourseCreateDialog {...props} />
      </Provider>
    );
  });

  test('create dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    const slug = wrapper.find('input[name="slug"]');
    slug.getDOMNode().value = 'new-course';
    slug.simulate('input');

    const name = wrapper.find('input[name="name"]');
    name.getDOMNode().value = 'New course';
    name.simulate('input');

    const description = wrapper.find('textarea[name="description"]');
    description.getDOMNode().value = 'New description';
    description.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateCourse).toHaveBeenCalledWith({
      slug: 'new-course',
      name: 'New course',
      description: 'New description',
    });
  });
});

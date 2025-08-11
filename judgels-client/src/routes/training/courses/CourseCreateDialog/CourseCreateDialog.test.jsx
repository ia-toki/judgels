import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';
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
    act(() => {
      const button = wrapper.find('button');
      button.simulate('click');
    });

    wrapper.update();

    act(() => {
      const slug = wrapper.find('input[name="slug"]');
      slug.prop('onChange')({ target: { value: 'new-course' } });

      const name = wrapper.find('input[name="name"]');
      name.prop('onChange')({ target: { value: 'New course' } });

      const description = wrapper.find('textarea[name="description"]');
      description.prop('onChange')({ target: { value: 'New description' } });

      const form = wrapper.find('form');
      form.simulate('submit');
    });

    expect(onCreateCourse).toHaveBeenCalledWith({
      slug: 'new-course',
      name: 'New course',
      description: 'New description',
    });
  });
});

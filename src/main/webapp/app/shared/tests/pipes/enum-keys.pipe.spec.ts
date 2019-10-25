import { EnumKeysPipe } from '../../pipes/enum-keys.pipe';

describe('EnumKeysPipe', () => {
  it('create an instance', () => {
    const pipe = new EnumKeysPipe();
    expect(pipe).toBeTruthy();
  });
});

package lib.bindable;

import android.util.AttributeSet;
import android.widget.RadioGroup;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BindableRadioGroupTest
{
    @Mock
    MockScopeContext contextMock;
    @Mock
    AttributeSet     attributeSetMock;

    @Before
    public void setup()
    {
        contextMock = mock(MockScopeContext.class);
        attributeSetMock = mock(AttributeSet.class);
        when(attributeSetMock.getAttributeValue(anyString(),anyString())).thenReturn("test");
        when(attributeSetMock.getAttributeCount()).thenReturn(1);
        when(attributeSetMock.getAttributeName(anyInt())).thenReturn("test");

        when(contextMock.getModel(anyString())).thenReturn("test");
    }

    @Test
    public void getExportableAttributes()
    {
        BindableRadioGroup underTest = new BindableRadioGroup(contextMock, attributeSetMock);
        AttributeSet map = underTest.getExportableAttributes();
        assertNotNull(map);
        assertTrue(map.getAttributeCount() == 1);
    }

    @Test
    public void getScope()
    {
        BindableRadioGroup underTest = new BindableRadioGroup(contextMock, attributeSetMock);
        assertEquals(contextMock,underTest.getScope());
    }

    @Test
    public void modelChanged()
    {
        BindableRadioGroup underTest = spy(new BindableRadioGroup(contextMock, attributeSetMock));

        underTest.modelChanged(contextMock);
        verify(underTest).setEnabled(anyBoolean());
    }

    @Test
    public void onCheckedChanged()
    {
        BindableRadioGroup underTest = spy(new BindableRadioGroup(contextMock, attributeSetMock));

        underTest.onCheckedChanged(mock(RadioGroup.class),0);
        verify(underTest).getExportableAttributes();
    }
}
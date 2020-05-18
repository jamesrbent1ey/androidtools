package lib.bindable;

import android.util.AttributeSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BindableTextViewTest
{
    @Mock
    MockScopeContext contextMock;
    @Mock
    AttributeSet attributeSetMock;

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
        // this test runs only when BindableTextView does not extend from AppCompatTextView
        BindableTextView textView = new BindableTextView(contextMock, attributeSetMock);
        AttributeSet map = textView.getExportableAttributes();
        assertNotNull(map);
        assertTrue(map.getAttributeCount() == 1);
    }

    @Test
    public void getScope()
    {
        BindableTextView textView = new BindableTextView(contextMock, attributeSetMock);
        assertEquals(contextMock,textView.getScope());
    }

    @Test
    public void modelChanged()
    {
        BindableTextView underTest = spy(new BindableTextView(contextMock, attributeSetMock));

        underTest.modelChanged(contextMock);
        verify(underTest).setText(anyString());
    }
}
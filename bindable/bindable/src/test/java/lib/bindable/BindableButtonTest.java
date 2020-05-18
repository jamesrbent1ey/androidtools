package lib.bindable;

import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Significant effort to test this - not much benefit since we can't test both -i.e. can't
 * do instrumented tests (required to construct the button) with static mocking (powermockito -
 * necessary to verify interactions)
 *
 * requires AppCompat classes which must be stubbed to allow instantiation
 */
public class BindableButtonTest
{
    @Mock
    MockScopeContext contextMock;
    @Mock
    AttributeSet attributeSetMock;
    @Mock
    View viewMock;
    @Mock
    KeyEvent keyEventMock;

    @Before
    public void setup()
    {
        contextMock = mock(MockScopeContext.class);
        attributeSetMock = mock(AttributeSet.class);
        when(attributeSetMock.getAttributeValue(anyString(),anyString())).thenReturn("test");
        when(attributeSetMock.getAttributeCount()).thenReturn(1);
        when(attributeSetMock.getAttributeName(anyInt())).thenReturn("test");

        when(contextMock.getModel(anyString())).thenReturn("test");

        viewMock = mock(View.class);
        keyEventMock = mock(KeyEvent.class);
    }

    // TODO: for androidTest: private Context context = ApplicationProvider.getApplicationContext();
    @Test
    public void test_getExportableAttributes()
    {
        /* TODO: for androidTest:
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.mock_bindable_button,null);
        BindableButton underTest = v.findViewById(R.id.underTest);

        Map<String,String> exportableAttributes = underTest.getExportableAttributes();
        assertNotNull(exportableAttributes);
        String val = exportableAttributes.get("test");
        assertTrue(val.equalsIgnoreCase("test"));
        */
        BindableButton underTest = new BindableButton(contextMock, attributeSetMock);
        AttributeSet map = underTest.getExportableAttributes();
        assertNotNull(map);
        assertTrue(map.getAttributeCount() == 1);
    }

    @Test
    public void test_getScope()
    {
        /* TODO: for androidTest:
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.mock_bindable_button,null);
        BindableButton underTest = v.findViewById(R.id.underTest);

        assertNull(underTest.getScope());
        */
        BindableButton underTest = new BindableButton(contextMock, attributeSetMock);
        assertEquals(contextMock,underTest.getScope());
    }

    @Test
    public void test_modelChanged()
    {
        BindableButton underTest = spy(new BindableButton(contextMock, attributeSetMock));

        underTest.modelChanged(contextMock);
        verify(underTest).setText(anyString());
    }

    @Test
    public void test_onClick()
    {
        BindableButton underTest = spy(new BindableButton(contextMock, attributeSetMock));

        underTest.onClick(viewMock);
        verify(underTest).getExportableAttributes();
    }

    @Test
    public void test_onKey()
    {
        BindableButton underTest = new BindableButton(contextMock, attributeSetMock);

        assertFalse(underTest.onKey(viewMock,0,keyEventMock));
    }

    @Test
    public void test_onLongClick()
    {
        BindableButton underTest = new BindableButton(contextMock, attributeSetMock);

        assertFalse(underTest.onLongClick(viewMock));
    }
}